package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AudioProcessingEngine
import com.example.data.PresetManager
import com.example.model.MicPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TestPlaybackMode {
  IDLE,
  PLAYING_RAW,
  PLAYING_BOOSTED
}

class VipMicViewModel(application: Application) : AndroidViewModel(application) {
  val audioEngine = AudioProcessingEngine(application.applicationContext)
  private val presetManager = PresetManager(application.applicationContext)

  // Master Power Toggle
  private val _isMasterOn = MutableStateFlow(false)
  val isMasterOn: StateFlow<Boolean> = _isMasterOn.asStateFlow()

  // Gain & Boost parameters
  private val _boostLevel = MutableStateFlow(2.2f)
  val boostLevel: StateFlow<Float> = _boostLevel.asStateFlow()

  private val _agcEnabled = MutableStateFlow(true)
  val agcEnabled: StateFlow<Boolean> = _agcEnabled.asStateFlow()

  private val _nsEnabled = MutableStateFlow(true)
  val nsEnabled: StateFlow<Boolean> = _nsEnabled.asStateFlow()

  private val _aecEnabled = MutableStateFlow(true)
  val aecEnabled: StateFlow<Boolean> = _aecEnabled.asStateFlow()

  private val _clarityBoostEnabled = MutableStateFlow(true)
  val clarityBoostEnabled: StateFlow<Boolean> = _clarityBoostEnabled.asStateFlow()

  private val _lowCutFilterEnabled = MutableStateFlow(true)
  val lowCutFilterEnabled: StateFlow<Boolean> = _lowCutFilterEnabled.asStateFlow()

  private val _earphoneOptimized = MutableStateFlow(true)
  val earphoneOptimized: StateFlow<Boolean> = _earphoneOptimized.asStateFlow()

  // Audio Engine passthroughs
  val inputLevelDb: StateFlow<Float> = audioEngine.inputLevelDb
  val peakLevel: StateFlow<Float> = audioEngine.peakLevel
  val waveformPoints: StateFlow<List<Float>> = audioEngine.waveformPoints
  val isHeadsetConnected: StateFlow<Boolean> = audioEngine.isHeadsetConnected
  val headsetName: StateFlow<String> = audioEngine.headsetName

  val isAgcAvailable: Boolean = audioEngine.isAgcAvailable
  val isNsAvailable: Boolean = audioEngine.isNsAvailable
  val isAecAvailable: Boolean = audioEngine.isAecAvailable

  // Presets state
  private val _presetsList = MutableStateFlow<List<MicPreset>>(emptyList())
  val presetsList: StateFlow<List<MicPreset>> = _presetsList.asStateFlow()

  private val _activePreset = MutableStateFlow<MicPreset?>(null)
  val activePreset: StateFlow<MicPreset?> = _activePreset.asStateFlow()

  // Live Monitor / Earphone Loopback
  private val _isLiveMonitoring = MutableStateFlow(false)
  val isLiveMonitoring: StateFlow<Boolean> = _isLiveMonitoring.asStateFlow()

  // Test recording and playback
  val isRecordingTest: StateFlow<Boolean> = audioEngine.isRecordingTest
  val hasTestRecording: StateFlow<Boolean> = audioEngine.hasTestRecording
  val testPlaybackProgress: StateFlow<Float> = audioEngine.testPlaybackProgress

  private val _testPlaybackMode = MutableStateFlow(TestPlaybackMode.IDLE)
  val testPlaybackMode: StateFlow<TestPlaybackMode> = _testPlaybackMode.asStateFlow()

  // Dialog & warning states
  private val _showFeedbackWarning = MutableStateFlow(false)
  val showFeedbackWarning: StateFlow<Boolean> = _showFeedbackWarning.asStateFlow()

  private val _showSavePresetDialog = MutableStateFlow(false)
  val showSavePresetDialog: StateFlow<Boolean> = _showSavePresetDialog.asStateFlow()

  private val _showManagePresetsDialog = MutableStateFlow(false)
  val showManagePresetsDialog: StateFlow<Boolean> = _showManagePresetsDialog.asStateFlow()

  private val _showHardwareNotice = MutableStateFlow(false)
  val showHardwareNotice: StateFlow<Boolean> = _showHardwareNotice.asStateFlow()

  private val _errorMessage = MutableStateFlow<String?>(null)
  val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

  init {
    loadPresets()
  }

  fun refreshHeadsetStatus() {
    audioEngine.updateHeadsetStatus()
  }

  private fun loadPresets() {
    val all = presetManager.getAllPresets()
    _presetsList.value = all
    val selectedId = presetManager.getSelectedPresetId()
    val match = all.find { it.id == selectedId } ?: all.firstOrNull()
    if (match != null) {
      applyPresetInternal(match)
    }
  }

  fun toggleMasterPower(hasPermission: Boolean) {
    if (!hasPermission) {
      _errorMessage.value = "Microphone permission is required to process audio."
      return
    }

    if (_isMasterOn.value) {
      audioEngine.stopEngine()
      _isMasterOn.value = false
      _isLiveMonitoring.value = false
    } else {
      syncEngineParams()
      val started = audioEngine.startEngine()
      if (started) {
        _isMasterOn.value = true
        _errorMessage.value = null
      } else {
        _errorMessage.value = "Failed to open microphone. Check if another app is using it."
      }
    }
  }

  fun setBoostLevel(level: Float) {
    _boostLevel.value = level.coerceIn(1.0f, 4.0f)
    syncEngineParams()
  }

  fun toggleAgc(enabled: Boolean) {
    _agcEnabled.value = enabled
    syncEngineParams()
  }

  fun toggleNs(enabled: Boolean) {
    _nsEnabled.value = enabled
    syncEngineParams()
  }

  fun toggleAec(enabled: Boolean) {
    _aecEnabled.value = enabled
    syncEngineParams()
  }

  fun toggleClarity(enabled: Boolean) {
    _clarityBoostEnabled.value = enabled
    syncEngineParams()
  }

  fun toggleLowCut(enabled: Boolean) {
    _lowCutFilterEnabled.value = enabled
    syncEngineParams()
  }

  fun toggleEarphoneOptimized(enabled: Boolean) {
    _earphoneOptimized.value = enabled
    syncEngineParams()
  }

  private fun syncEngineParams() {
    audioEngine.updateEffectStates(
      gain = _boostLevel.value,
      agc = _agcEnabled.value,
      ns = _nsEnabled.value,
      aec = _aecEnabled.value,
      clarity = _clarityBoostEnabled.value,
      lowCut = _lowCutFilterEnabled.value,
      earphoneOpt = _earphoneOptimized.value
    )
  }

  fun selectPreset(preset: MicPreset) {
    applyPresetInternal(preset)
    presetManager.saveSelectedPresetId(preset.id)
  }

  private fun applyPresetInternal(preset: MicPreset) {
    _activePreset.value = preset
    _boostLevel.value = preset.boostLevel
    _agcEnabled.value = preset.agcEnabled
    _nsEnabled.value = preset.noiseSuppressionEnabled
    _aecEnabled.value = preset.echoCancelerEnabled
    _clarityBoostEnabled.value = preset.clarityBoostEnabled
    _lowCutFilterEnabled.value = preset.lowCutFilterEnabled
    _earphoneOptimized.value = preset.earphoneOptimized
    syncEngineParams()
  }

  fun saveCustomPreset(name: String, description: String) {
    if (name.isBlank()) return
    val newPreset = MicPreset(
      id = "",
      name = name.trim(),
      isBuiltIn = false,
      boostLevel = _boostLevel.value,
      agcEnabled = _agcEnabled.value,
      noiseSuppressionEnabled = _nsEnabled.value,
      echoCancelerEnabled = _aecEnabled.value,
      clarityBoostEnabled = _clarityBoostEnabled.value,
      lowCutFilterEnabled = _lowCutFilterEnabled.value,
      earphoneOptimized = _earphoneOptimized.value,
      description = description.ifBlank { "Custom user VIP preset" }
    )
    val saved = presetManager.saveCustomPreset(newPreset)
    loadPresets()
    _activePreset.value = saved
    _showSavePresetDialog.value = false
  }

  fun deletePreset(id: String) {
    presetManager.deleteCustomPreset(id)
    loadPresets()
  }

  fun toggleLiveMonitoring(hasPermission: Boolean) {
    if (!hasPermission) {
      _errorMessage.value = "Microphone permission is required."
      return
    }

    if (_isLiveMonitoring.value) {
      audioEngine.setLiveMonitoring(false)
      _isLiveMonitoring.value = false
      return
    }

    // Safety check: if no headphones are plugged in, warn user to prevent feedback screech
    if (!isHeadsetConnected.value) {
      _showFeedbackWarning.value = true
    } else {
      enableMonitoringInternal()
    }
  }

  fun confirmFeedbackWarningAndEnable() {
    _showFeedbackWarning.value = false
    enableMonitoringInternal()
  }

  fun dismissFeedbackWarning() {
    _showFeedbackWarning.value = false
  }

  private fun enableMonitoringInternal() {
    if (!_isMasterOn.value) {
      syncEngineParams()
      audioEngine.startEngine()
      _isMasterOn.value = true
    }
    audioEngine.setLiveMonitoring(true)
    _isLiveMonitoring.value = true
  }

  fun start5SecTest(hasPermission: Boolean) {
    if (!hasPermission) {
      _errorMessage.value = "Microphone permission is required for testing."
      return
    }
    if (!_isMasterOn.value) {
      syncEngineParams()
      audioEngine.startEngine()
      _isMasterOn.value = true
    }
    audioEngine.startTestRecording {
      // Completed recording
    }
  }

  fun cancelTestRecording() {
    audioEngine.cancelTestRecording()
  }

  fun playTestRaw() {
    _testPlaybackMode.value = TestPlaybackMode.PLAYING_RAW
    audioEngine.playTestRecording(withBoost = false) {
      _testPlaybackMode.value = TestPlaybackMode.IDLE
    }
  }

  fun playTestBoosted() {
    _testPlaybackMode.value = TestPlaybackMode.PLAYING_BOOSTED
    audioEngine.playTestRecording(withBoost = true) {
      _testPlaybackMode.value = TestPlaybackMode.IDLE
    }
  }

  fun stopTestPlayback() {
    audioEngine.stopTestPlayback()
    _testPlaybackMode.value = TestPlaybackMode.IDLE
  }

  fun setShowSavePresetDialog(show: Boolean) {
    _showSavePresetDialog.value = show
  }

  fun setShowManagePresetsDialog(show: Boolean) {
    _showManagePresetsDialog.value = show
  }

  fun setShowHardwareNotice(show: Boolean) {
    _showHardwareNotice.value = show
  }

  fun clearError() {
    _errorMessage.value = null
  }

  override fun onCleared() {
    super.onCleared()
    audioEngine.stopEngine()
  }
}
