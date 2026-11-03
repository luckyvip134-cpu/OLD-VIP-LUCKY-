package com.example.audio

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioDeviceInfo
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioRecord
import android.media.AudioTrack
import android.media.MediaRecorder
import android.media.audiofx.AcousticEchoCanceler
import android.media.audiofx.AutomaticGainControl
import android.media.audiofx.NoiseSuppressor
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import kotlin.math.abs
import kotlin.math.log10
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt
import kotlin.math.tanh

class AudioProcessingEngine(private val context: Context) {
  private val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

  companion object {
    private const val TAG = "AudioProcessingEngine"
    const val SAMPLE_RATE = 44100
    private const val CHANNEL_IN = AudioFormat.CHANNEL_IN_MONO
    private const val CHANNEL_OUT = AudioFormat.CHANNEL_OUT_MONO
    private const val AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT
    const val MAX_TEST_RECORD_DURATION_MS = 5000L
  }

  // Live audio states
  private val _inputLevelDb = MutableStateFlow(-60f)
  val inputLevelDb: StateFlow<Float> = _inputLevelDb.asStateFlow()

  private val _peakLevel = MutableStateFlow(0f)
  val peakLevel: StateFlow<Float> = _peakLevel.asStateFlow()

  private val _waveformPoints = MutableStateFlow<List<Float>>(List(32) { 0f })
  val waveformPoints: StateFlow<List<Float>> = _waveformPoints.asStateFlow()

  private val _isHeadsetConnected = MutableStateFlow(false)
  val isHeadsetConnected: StateFlow<Boolean> = _isHeadsetConnected.asStateFlow()

  private val _headsetName = MutableStateFlow("Phone Built-in Mic")
  val headsetName: StateFlow<String> = _headsetName.asStateFlow()

  // Hardware FX capabilities
  val isAgcAvailable: Boolean by lazy { AutomaticGainControl.isAvailable() }
  val isNsAvailable: Boolean by lazy { NoiseSuppressor.isAvailable() }
  val isAecAvailable: Boolean by lazy { AcousticEchoCanceler.isAvailable() }

  // Active hardware effects
  private var agcEffect: AutomaticGainControl? = null
  private var nsEffect: NoiseSuppressor? = null
  private var aecEffect: AcousticEchoCanceler? = null

  // Audio threads & jobs
  private var audioRecord: AudioRecord? = null
  private var monitorAudioTrack: AudioTrack? = null
  private var processingJob: Job? = null
  private val engineScope = CoroutineScope(Dispatchers.Default)

  // Current DSP parameters
  @Volatile var gainMultiplier: Float = 2.0f
  @Volatile var isAgcRequested: Boolean = true
  @Volatile var isNsRequested: Boolean = true
  @Volatile var isAecRequested: Boolean = true
  @Volatile var isClarityBoostEnabled: Boolean = true
  @Volatile var isLowCutFilterEnabled: Boolean = true
  @Volatile var isEarphoneOptimized: Boolean = true
  @Volatile var isLiveMonitorActive: Boolean = false

  // Test recording buffers
  private val recordedRawBuffer = ByteArrayOutputStream()
  private val _isRecordingTest = MutableStateFlow(false)
  val isRecordingTest: StateFlow<Boolean> = _isRecordingTest.asStateFlow()

  private val _isPlayingTest = MutableStateFlow(false)
  val isPlayingTest: StateFlow<Boolean> = _isPlayingTest.asStateFlow()

  private val _hasTestRecording = MutableStateFlow(false)
  val hasTestRecording: StateFlow<Boolean> = _hasTestRecording.asStateFlow()

  private val _testPlaybackProgress = MutableStateFlow(0f)
  val testPlaybackProgress: StateFlow<Float> = _testPlaybackProgress.asStateFlow()

  private var testPlaybackJob: Job? = null

  // IIR Filter states for low-cut and clarity EQ
  private var lastHighPassIn = 0f
  private var lastHighPassOut = 0f
  private var lastLowPassIn = 0f
  private var lastLowPassOut = 0f

  init {
    updateHeadsetStatus()
  }

  fun updateHeadsetStatus() {
    var detected = false
    var name = "Phone Built-in Mic"

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val devices = audioManager.getDevices(AudioManager.GET_DEVICES_OUTPUTS)
      for (dev in devices) {
        when (dev.type) {
          AudioDeviceInfo.TYPE_WIRED_HEADSET,
          AudioDeviceInfo.TYPE_WIRED_HEADPHONES,
          AudioDeviceInfo.TYPE_USB_HEADSET -> {
            detected = true
            name = "Wired Headset / Earphones"
            break
          }
          AudioDeviceInfo.TYPE_BLUETOOTH_A2DP,
          AudioDeviceInfo.TYPE_BLUETOOTH_SCO,
          AudioDeviceInfo.TYPE_BLE_HEADSET -> {
            detected = true
            name = "Bluetooth Headset / Earphones"
            break
          }
        }
      }
    } else {
      @Suppress("DEPRECATION")
      if (audioManager.isWiredHeadsetOn) {
        detected = true
        name = "Wired Headset / Earphones"
      }
    }

    _isHeadsetConnected.value = detected
    _headsetName.value = name
  }

  @SuppressLint("MissingPermission")
  fun startEngine(): Boolean {
    if (processingJob != null && processingJob?.isActive == true) {
      return true
    }

    val minBufSize = AudioRecord.getMinBufferSize(
      SAMPLE_RATE,
      CHANNEL_IN,
      AUDIO_ENCODING
    )

    if (minBufSize <= 0) {
      Log.e(TAG, "Invalid buffer size: $minBufSize")
      return false
    }

    val bufferSize = max(minBufSize, 2048)

    try {
      // Try VOICE_COMMUNICATION first for native earphone/mic beamforming, fallback to MIC
      val recordSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION
      val record = AudioRecord(
        recordSource,
        SAMPLE_RATE,
        CHANNEL_IN,
        AUDIO_ENCODING,
        bufferSize
      )

      if (record.state != AudioRecord.STATE_INITIALIZED) {
        record.release()
        // Fallback to standard MIC source
        val fallbackRecord = AudioRecord(
          MediaRecorder.AudioSource.MIC,
          SAMPLE_RATE,
          CHANNEL_IN,
          AUDIO_ENCODING,
          bufferSize
        )
        if (fallbackRecord.state != AudioRecord.STATE_INITIALIZED) {
          fallbackRecord.release()
          return false
        }
        audioRecord = fallbackRecord
      } else {
        audioRecord = record
      }

      val sessionId = audioRecord?.audioSessionId ?: 0

      // Setup hardware effects if available
      setupHardwareFx(sessionId)

      audioRecord?.startRecording()

      // Start processing loop
      processingJob = engineScope.launch {
        processAudioLoop(bufferSize)
      }
      return true
    } catch (e: Exception) {
      Log.e(TAG, "Failed to start audio engine", e)
      stopEngine()
      return false
    }
  }

  private fun setupHardwareFx(sessionId: Int) {
    if (sessionId == 0) return

    try {
      if (isAgcAvailable && agcEffect == null) {
        agcEffect = AutomaticGainControl.create(sessionId)?.apply {
          enabled = isAgcRequested
        }
      }
      if (isNsAvailable && nsEffect == null) {
        nsEffect = NoiseSuppressor.create(sessionId)?.apply {
          enabled = isNsRequested
        }
      }
      if (isAecAvailable && aecEffect == null) {
        aecEffect = AcousticEchoCanceler.create(sessionId)?.apply {
          enabled = isAecRequested
        }
      }
    } catch (e: Exception) {
      Log.w(TAG, "Could not initialize hardware audio effects", e)
    }
  }

  fun updateEffectStates(
    gain: Float,
    agc: Boolean,
    ns: Boolean,
    aec: Boolean,
    clarity: Boolean,
    lowCut: Boolean,
    earphoneOpt: Boolean
  ) {
    gainMultiplier = gain
    isAgcRequested = agc
    isNsRequested = ns
    isAecRequested = aec
    isClarityBoostEnabled = clarity
    isLowCutFilterEnabled = lowCut
    isEarphoneOptimized = earphoneOpt

    try {
      agcEffect?.enabled = agc
      nsEffect?.enabled = ns
      aecEffect?.enabled = aec
    } catch (e: Exception) {
      Log.w(TAG, "Error updating hardware effect states", e)
    }
  }

  fun setLiveMonitoring(enabled: Boolean) {
    isLiveMonitorActive = enabled
    if (enabled) {
      initMonitorTrack()
    } else {
      stopMonitorTrack()
    }
  }

  private fun initMonitorTrack() {
    if (monitorAudioTrack != null) return
    try {
      val minTrackBuf = AudioTrack.getMinBufferSize(
        SAMPLE_RATE,
        CHANNEL_OUT,
        AUDIO_ENCODING
      )
      val bufSize = max(minTrackBuf, 2048)
      monitorAudioTrack = AudioTrack.Builder()
        .setAudioFormat(
          AudioFormat.Builder()
            .setEncoding(AUDIO_ENCODING)
            .setSampleRate(SAMPLE_RATE)
            .setChannelMask(CHANNEL_OUT)
            .build()
        )
        .setBufferSizeInBytes(bufSize)
        .setTransferMode(AudioTrack.MODE_STREAM)
        .build().apply {
          play()
        }
    } catch (e: Exception) {
      Log.e(TAG, "Failed to initialize monitor AudioTrack", e)
    }
  }

  private fun stopMonitorTrack() {
    try {
      monitorAudioTrack?.stop()
      monitorAudioTrack?.release()
    } catch (e: Exception) {
      Log.w(TAG, "Error stopping monitor track", e)
    } finally {
      monitorAudioTrack = null
    }
  }

  private suspend fun processAudioLoop(bufferSize: Int) {
    val shortBuffer = ShortArray(bufferSize / 2)
    val processedBuffer = ShortArray(shortBuffer.size)
    var smoothedDb = -60f
    var smoothedPeak = 0f

    while (engineScope.isActive && audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
      val readCount = audioRecord?.read(shortBuffer, 0, shortBuffer.size) ?: 0
      if (readCount > 0) {
        var sumSquares = 0.0
        var maxAmp = 0

        // Calculate active gain
        val activeGain = if (isEarphoneOptimized && _isHeadsetConnected.value) {
          gainMultiplier * 1.15f // Extra sensitivity contour for small headset mics
        } else {
          gainMultiplier
        }

        // Apply DSP processing
        for (i in 0 until readCount) {
          var sample = shortBuffer[i].toFloat()

          // 1. Optional Low-Cut Filter (~80Hz high-pass) to remove mic friction / rumble
          if (isLowCutFilterEnabled) {
            val alpha = 0.988f
            val hpOut = alpha * (lastHighPassOut + sample - lastHighPassIn)
            lastHighPassIn = sample
            lastHighPassOut = hpOut
            sample = hpOut
          }

          // 2. Optional Vocal Clarity presence boost (~3.5kHz enhancement)
          if (isClarityBoostEnabled) {
            val alphaLp = 0.75f
            val lpOut = lastLowPassOut + alphaLp * (sample - lastLowPassOut)
            lastLowPassIn = sample
            lastLowPassOut = lpOut
            val highDetail = sample - lpOut
            sample += highDetail * 0.45f
          }

          // 3. Microphone Boost Amplification with Soft-knee Saturation Limiter
          val boosted = (sample / 32768f) * activeGain
          val limited = if (boosted > 0.95f || boosted < -0.95f) {
            tanh(boosted.toDouble()).toFloat()
          } else {
            boosted
          }

          val outSample = (limited * 32767f).toInt().coerceIn(-32768, 32767).toShort()
          processedBuffer[i] = outSample

          sumSquares += outSample * outSample
          val absSample = abs(outSample.toInt())
          if (absSample > maxAmp) {
            maxAmp = absSample
          }
        }

        // Record for test feature if active
        if (_isRecordingTest.value) {
          val byteBuf = ByteArray(readCount * 2)
          for (i in 0 until readCount) {
            val s = shortBuffer[i].toInt()
            byteBuf[i * 2] = (s and 0xFF).toByte()
            byteBuf[i * 2 + 1] = ((s shr 8) and 0xFF).toByte()
          }
          synchronized(recordedRawBuffer) {
            recordedRawBuffer.write(byteBuf)
          }
        }

        // Live monitor loopback (if user is testing via earphones)
        if (isLiveMonitorActive && monitorAudioTrack?.playState == AudioTrack.PLAYSTATE_PLAYING) {
          monitorAudioTrack?.write(processedBuffer, 0, readCount)
        }

        // Metrics calculation (RMS dBFS)
        val rms = sqrt(sumSquares / readCount)
        val rawDb = if (rms > 1.0) {
          (20 * log10(rms / 32768.0)).toFloat().coerceIn(-60f, 0f)
        } else {
          -60f
        }
        val peakRatio = (maxAmp / 32768f).coerceIn(0f, 1f)

        // Smooth decay for visual VU meter
        smoothedDb = if (rawDb > smoothedDb) {
          rawDb
        } else {
          smoothedDb * 0.85f + rawDb * 0.15f
        }
        smoothedPeak = if (peakRatio > smoothedPeak) {
          peakRatio
        } else {
          smoothedPeak * 0.90f
        }

        _inputLevelDb.value = smoothedDb
        _peakLevel.value = smoothedPeak

        // Waveform preview points (32 samples evenly sampled)
        val step = max(1, readCount / 32)
        val points = mutableListOf<Float>()
        for (i in 0 until 32) {
          val idx = min(i * step, readCount - 1)
          points.add((processedBuffer[idx] / 32768f).coerceIn(-1f, 1f))
        }
        _waveformPoints.value = points
      }
      delay(16) // ~60fps refresh rate
    }
  }

  // 5-Second Test Recording
  fun startTestRecording(onComplete: () -> Unit) {
    synchronized(recordedRawBuffer) {
      recordedRawBuffer.reset()
    }
    _isRecordingTest.value = true
    _hasTestRecording.value = false

    engineScope.launch {
      val startTime = System.currentTimeMillis()
      while (System.currentTimeMillis() - startTime < MAX_TEST_RECORD_DURATION_MS) {
        if (!_isRecordingTest.value) break
        delay(100)
      }
      _isRecordingTest.value = false
      synchronized(recordedRawBuffer) {
        if (recordedRawBuffer.size() > 0) {
          _hasTestRecording.value = true
        }
      }
      onComplete()
    }
  }

  fun cancelTestRecording() {
    _isRecordingTest.value = false
  }

  // Playback Test with either Raw or VIP Boosted processing
  fun playTestRecording(withBoost: Boolean, onFinished: () -> Unit) {
    stopTestPlayback()

    val rawBytes: ByteArray
    synchronized(recordedRawBuffer) {
      rawBytes = recordedRawBuffer.toByteArray()
    }

    if (rawBytes.isEmpty()) {
      onFinished()
      return
    }

    _isPlayingTest.value = true
    _testPlaybackProgress.value = 0f

    testPlaybackJob = engineScope.launch {
      try {
        val totalShorts = rawBytes.size / 2
        val shortArray = ShortArray(totalShorts)
        for (i in 0 until totalShorts) {
          val b1 = rawBytes[i * 2].toInt() and 0xFF
          val b2 = rawBytes[i * 2 + 1].toInt()
          shortArray[i] = ((b2 shl 8) or b1).toShort()
        }

        // Apply chosen processing
        val outputShorts = ShortArray(totalShorts)
        val gain = if (withBoost) gainMultiplier else 1.0f

        for (i in 0 until totalShorts) {
          if (!withBoost) {
            outputShorts[i] = shortArray[i]
          } else {
            var s = shortArray[i].toFloat()
            if (isClarityBoostEnabled) {
              s *= 1.1f
            }
            val boosted = (s / 32768f) * gain
            val limited = if (boosted > 0.95f || boosted < -0.95f) {
              tanh(boosted.toDouble()).toFloat()
            } else {
              boosted
            }
            outputShorts[i] = (limited * 32767f).toInt().coerceIn(-32768, 32767).toShort()
          }
        }

        val minTrackBuf = AudioTrack.getMinBufferSize(
          SAMPLE_RATE,
          CHANNEL_OUT,
          AUDIO_ENCODING
        )
        val track = AudioTrack.Builder()
          .setAudioFormat(
            AudioFormat.Builder()
              .setEncoding(AUDIO_ENCODING)
              .setSampleRate(SAMPLE_RATE)
              .setChannelMask(CHANNEL_OUT)
              .build()
          )
          .setBufferSizeInBytes(max(minTrackBuf, totalShorts * 2))
          .setTransferMode(AudioTrack.MODE_STREAM)
          .build()

        track.play()

        val chunkSize = 1024
        var offset = 0
        while (isActive && offset < totalShorts) {
          val toWrite = min(chunkSize, totalShorts - offset)
          track.write(outputShorts, offset, toWrite)
          offset += toWrite
          _testPlaybackProgress.value = offset.toFloat() / totalShorts.toFloat()
          delay(15)
        }

        track.stop()
        track.release()
      } catch (e: Exception) {
        Log.e(TAG, "Error playing test recording", e)
      } finally {
        _isPlayingTest.value = false
        _testPlaybackProgress.value = 0f
        onFinished()
      }
    }
  }

  fun stopTestPlayback() {
    testPlaybackJob?.cancel()
    testPlaybackJob = null
    _isPlayingTest.value = false
    _testPlaybackProgress.value = 0f
  }

  fun stopEngine() {
    setLiveMonitoring(false)
    stopTestPlayback()
    _isRecordingTest.value = false

    processingJob?.cancel()
    processingJob = null

    try {
      agcEffect?.release()
      nsEffect?.release()
      aecEffect?.release()
    } catch (_: Exception) {}
    agcEffect = null
    nsEffect = null
    aecEffect = null

    try {
      if (audioRecord?.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
        audioRecord?.stop()
      }
      audioRecord?.release()
    } catch (_: Exception) {}
    audioRecord = null

    _inputLevelDb.value = -60f
    _peakLevel.value = 0f
    _waveformPoints.value = List(32) { 0f }
  }
}
