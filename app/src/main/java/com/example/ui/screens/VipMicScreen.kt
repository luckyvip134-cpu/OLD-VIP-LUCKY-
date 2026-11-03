package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.FeedbackWarningDialog
import com.example.ui.components.HardwareNoticeDialog
import com.example.ui.components.ManagePresetsDialog
import com.example.ui.components.SavePresetDialog
import com.example.ui.components.VipBoostSlider
import com.example.ui.components.VipHardwareFxSection
import com.example.ui.components.VipHeader
import com.example.ui.components.VipMeterSection
import com.example.ui.components.VipPresetsQuickBar
import com.example.ui.components.VipTestStudioCard
import com.example.ui.theme.MeterOrange
import com.example.ui.theme.VipBlack
import com.example.ui.theme.VipCardBorder
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary
import com.example.viewmodel.VipMicViewModel

@Composable
fun VipMicScreen(
  viewModel: VipMicViewModel,
  modifier: Modifier = Modifier
) {
  val context = LocalContext.current
  val snackbarHostState = remember { SnackbarHostState() }

  // Check initial permission
  var hasRecordPermission by remember {
    mutableStateOf(
      ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.RECORD_AUDIO
      ) == PackageManager.PERMISSION_GRANTED
    )
  }

  val permissionLauncher = rememberLauncherForActivityResult(
    contract = ActivityResultContracts.RequestPermission()
  ) { isGranted ->
    hasRecordPermission = isGranted
    if (isGranted) {
      viewModel.toggleMasterPower(hasPermission = true)
    }
  }

  // Lifecycle headset polling
  DisposableEffect(Unit) {
    viewModel.refreshHeadsetStatus()
    onDispose { }
  }

  // ViewModel state observers
  val isMasterOn by viewModel.isMasterOn.collectAsStateWithLifecycle()
  val boostLevel by viewModel.boostLevel.collectAsStateWithLifecycle()
  val agcEnabled by viewModel.agcEnabled.collectAsStateWithLifecycle()
  val nsEnabled by viewModel.nsEnabled.collectAsStateWithLifecycle()
  val aecEnabled by viewModel.aecEnabled.collectAsStateWithLifecycle()
  val clarityEnabled by viewModel.clarityBoostEnabled.collectAsStateWithLifecycle()
  val lowCutEnabled by viewModel.lowCutFilterEnabled.collectAsStateWithLifecycle()
  val earphoneOptEnabled by viewModel.earphoneOptimized.collectAsStateWithLifecycle()

  val inputLevelDb by viewModel.inputLevelDb.collectAsStateWithLifecycle()
  val peakLevel by viewModel.peakLevel.collectAsStateWithLifecycle()
  val waveformPoints by viewModel.waveformPoints.collectAsStateWithLifecycle()

  val isHeadsetConnected by viewModel.isHeadsetConnected.collectAsStateWithLifecycle()
  val headsetName by viewModel.headsetName.collectAsStateWithLifecycle()

  val presetsList by viewModel.presetsList.collectAsStateWithLifecycle()
  val activePreset by viewModel.activePreset.collectAsStateWithLifecycle()

  val isLiveMonitoring by viewModel.isLiveMonitoring.collectAsStateWithLifecycle()
  val isRecordingTest by viewModel.isRecordingTest.collectAsStateWithLifecycle()
  val hasTestRecording by viewModel.hasTestRecording.collectAsStateWithLifecycle()
  val testPlaybackMode by viewModel.testPlaybackMode.collectAsStateWithLifecycle()
  val testPlaybackProgress by viewModel.testPlaybackProgress.collectAsStateWithLifecycle()

  val showFeedbackWarning by viewModel.showFeedbackWarning.collectAsStateWithLifecycle()
  val showSavePresetDialog by viewModel.showSavePresetDialog.collectAsStateWithLifecycle()
  val showManagePresetsDialog by viewModel.showManagePresetsDialog.collectAsStateWithLifecycle()
  val showHardwareNotice by viewModel.showHardwareNotice.collectAsStateWithLifecycle()
  val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()

  Scaffold(
    modifier = modifier
      .fillMaxSize()
      .background(VipBlack),
    containerColor = VipBlack,
    contentWindowInsets = WindowInsets.safeDrawing,
    snackbarHost = { SnackbarHost(snackbarHostState) }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
        .verticalScroll(rememberScrollState())
    ) {
      // 1. VIP Header
      VipHeader(
        activePresetName = activePreset?.name ?: "Custom Tuning",
        isHeadsetConnected = isHeadsetConnected,
        headsetName = headsetName,
        onOpenPresets = { viewModel.setShowManagePresetsDialog(true) },
        onOpenInfo = { viewModel.setShowHardwareNotice(true) }
      )

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Microphone Permission Banner (if not granted)
        if (!hasRecordPermission) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(12.dp))
              .background(Color(0xFF2E1015))
              .border(1.dp, VipRedNeon, RoundedCornerShape(12.dp))
              .padding(14.dp)
          ) {
            Column {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = Icons.Default.Lock,
                  contentDescription = null,
                  tint = VipRedNeon,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "Microphone Permission Required",
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp,
                  color = Color.White
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "To amplify volume and display live audio input levels, OLD VIP Lucky Mic requires microphone access.",
                fontSize = 11.sp,
                color = VipTextSecondary
              )
              Spacer(modifier = Modifier.height(10.dp))
              Button(
                onClick = { permissionLauncher.launch(Manifest.permission.RECORD_AUDIO) },
                modifier = Modifier
                  .testTag("grant_permission_button")
                  .fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                  containerColor = VipRedPrimary,
                  contentColor = Color.White
                ),
                shape = RoundedCornerShape(8.dp)
              ) {
                Icon(Icons.Default.Mic, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Grant Microphone Permission", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }

        // Error message banner
        AnimatedVisibility(
          visible = errorMessage != null,
          enter = fadeIn(),
          exit = fadeOut()
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .background(Color(0xFF3B1218))
              .border(1.dp, VipRedNeon, RoundedCornerShape(10.dp))
              .padding(10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(
                  imageVector = Icons.Default.Warning,
                  contentDescription = null,
                  tint = VipRedNeon,
                  modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = errorMessage ?: "",
                  fontSize = 11.sp,
                  color = Color.White
                )
              }
              IconButton(
                onClick = { viewModel.clearError() },
                modifier = Modifier.size(24.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "Dismiss error",
                  tint = VipTextSecondary,
                  modifier = Modifier.size(14.dp)
                )
              }
            }
          }
        }

        // 2. VIP Meter & Power Section
        VipMeterSection(
          isMasterOn = isMasterOn,
          inputLevelDb = inputLevelDb,
          peakLevel = peakLevel,
          waveformPoints = waveformPoints,
          onToggleMasterPower = {
            if (!hasRecordPermission) {
              permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
              viewModel.toggleMasterPower(hasPermission = true)
            }
          }
        )

        // 3. Boost Slider Card
        VipBoostSlider(
          boostLevel = boostLevel,
          onBoostLevelChange = { viewModel.setBoostLevel(it) }
        )

        // 4. Sound Presets Quick Bar
        VipPresetsQuickBar(
          presets = presetsList,
          activePreset = activePreset,
          onSelectPreset = { viewModel.selectPreset(it) },
          onOpenSavePresetDialog = { viewModel.setShowSavePresetDialog(true) },
          onOpenManagePresetsDialog = { viewModel.setShowManagePresetsDialog(true) }
        )

        // 5. Audio Processing & Hardware FX
        VipHardwareFxSection(
          isAgcAvailable = viewModel.isAgcAvailable,
          agcEnabled = agcEnabled,
          onAgcChange = { viewModel.toggleAgc(it) },
          isNsAvailable = viewModel.isNsAvailable,
          nsEnabled = nsEnabled,
          onNsChange = { viewModel.toggleNs(it) },
          isAecAvailable = viewModel.isAecAvailable,
          aecEnabled = aecEnabled,
          onAecChange = { viewModel.toggleAec(it) },
          clarityEnabled = clarityEnabled,
          onClarityChange = { viewModel.toggleClarity(it) },
          lowCutEnabled = lowCutEnabled,
          onLowCutChange = { viewModel.toggleLowCut(it) },
          earphoneOptEnabled = earphoneOptEnabled,
          onEarphoneOptChange = { viewModel.toggleEarphoneOptimized(it) },
          isHeadsetConnected = isHeadsetConnected,
          headsetName = headsetName
        )

        // 6. Microphone Test Studio Card
        VipTestStudioCard(
          isLiveMonitoring = isLiveMonitoring,
          onToggleLiveMonitoring = {
            if (!hasRecordPermission) {
              permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
              viewModel.toggleLiveMonitoring(hasPermission = true)
            }
          },
          isRecordingTest = isRecordingTest,
          hasTestRecording = hasTestRecording,
          testPlaybackMode = testPlaybackMode,
          testPlaybackProgress = testPlaybackProgress,
          onStart5SecTest = {
            if (!hasRecordPermission) {
              permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
            } else {
              viewModel.start5SecTest(hasPermission = true)
            }
          },
          onCancelTestRecording = { viewModel.cancelTestRecording() },
          onPlayTestRaw = { viewModel.playTestRaw() },
          onPlayTestBoosted = { viewModel.playTestBoosted() },
          onStopTestPlayback = { viewModel.stopTestPlayback() },
          isHeadsetConnected = isHeadsetConnected
        )

        // 7. Transparent Limitations & Compatibility Card
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF140B0E))
            .border(1.dp, Color(0xFF2B1216), RoundedCornerShape(14.dp))
            .padding(14.dp)
        ) {
          Row(verticalAlignment = Alignment.Top) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = VipGold,
              modifier = Modifier
                .size(18.dp)
                .padding(top = 1.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "Device Hardware & App Compatibility Notice",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = VipTextPrimary
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "• Boost is constrained by physical microphone ADC hardware.\n" +
                    "• Third-party apps (e.g. WhatsApp calls) operate within isolated Android audio sessions for privacy and may not route through external third-party boosters.",
                fontSize = 10.sp,
                color = VipTextSecondary,
                lineHeight = 14.sp
              )
            }
          }
        }

        Spacer(modifier = Modifier.height(24.dp))
      }
    }
  }

  // Dialogs
  if (showSavePresetDialog) {
    SavePresetDialog(
      currentBoostLevel = boostLevel,
      agcEnabled = agcEnabled,
      nsEnabled = nsEnabled,
      aecEnabled = aecEnabled,
      clarityEnabled = clarityEnabled,
      lowCutEnabled = lowCutEnabled,
      onSave = { name, desc ->
        viewModel.saveCustomPreset(name, desc)
      },
      onDismiss = { viewModel.setShowSavePresetDialog(false) }
    )
  }

  if (showManagePresetsDialog) {
    ManagePresetsDialog(
      presets = presetsList,
      activePresetId = activePreset?.id,
      onSelectPreset = { preset ->
        viewModel.selectPreset(preset)
      },
      onDeletePreset = { id ->
        viewModel.deletePreset(id)
      },
      onOpenSaveNew = {
        viewModel.setShowSavePresetDialog(true)
      },
      onDismiss = { viewModel.setShowManagePresetsDialog(false) }
    )
  }

  if (showFeedbackWarning) {
    FeedbackWarningDialog(
      onConfirmProceed = { viewModel.confirmFeedbackWarningAndEnable() },
      onDismiss = { viewModel.dismissFeedbackWarning() }
    )
  }

  if (showHardwareNotice) {
    HardwareNoticeDialog(
      onDismiss = { viewModel.setShowHardwareNotice(false) }
    )
  }
}
