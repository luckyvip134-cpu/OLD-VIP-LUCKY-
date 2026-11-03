package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MeterGreen
import com.example.ui.theme.VipCardBorder
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldSubtle
import com.example.ui.theme.VipRedDark
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary

@Composable
fun VipHardwareFxSection(
  isAgcAvailable: Boolean,
  agcEnabled: Boolean,
  onAgcChange: (Boolean) -> Unit,
  isNsAvailable: Boolean,
  nsEnabled: Boolean,
  onNsChange: (Boolean) -> Unit,
  isAecAvailable: Boolean,
  aecEnabled: Boolean,
  onAecChange: (Boolean) -> Unit,
  clarityEnabled: Boolean,
  onClarityChange: (Boolean) -> Unit,
  lowCutEnabled: Boolean,
  onLowCutChange: (Boolean) -> Unit,
  earphoneOptEnabled: Boolean,
  onEarphoneOptChange: (Boolean) -> Unit,
  isHeadsetConnected: Boolean,
  headsetName: String,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(VipCardSurface)
      .border(1.dp, VipCardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    // Title
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(Color(0xFF330E14)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.GraphicEq,
            contentDescription = null,
            tint = VipGold,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "AUDIO PROCESSING & HARDWARE FX",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = VipTextPrimary,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Android audiofx & voice enhancement suite",
            fontSize = 11.sp,
            color = VipTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Earphone Optimization Highlight Card
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(
          if (isHeadsetConnected) Color(0xFF1F140A) else Color(0xFF140B0E)
        )
        .border(
          1.dp,
          if (isHeadsetConnected) VipGold.copy(alpha = 0.5f) else VipCardBorder,
          RoundedCornerShape(12.dp)
        )
        .padding(12.dp)
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
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = if (isHeadsetConnected) VipGold else VipTextMuted,
            modifier = Modifier.size(24.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "Earphone Mic Optimization",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = if (isHeadsetConnected) VipGold else VipTextPrimary
              )
              if (isHeadsetConnected) {
                Spacer(modifier = Modifier.width(6.dp))
                Box(
                  modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(MeterGreen.copy(alpha = 0.2f))
                    .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                  Text(
                    text = "ACTIVE",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = MeterGreen
                  )
                }
              }
            }
            Text(
              text = if (isHeadsetConnected) "$headsetName connected. Sensitivity calibrated."
              else "Calibrates gain & removes clothing rustle on wired headsets.",
              fontSize = 11.sp,
              color = VipTextSecondary
            )
          }
        }
        Switch(
          checked = earphoneOptEnabled,
          onCheckedChange = onEarphoneOptChange,
          modifier = Modifier.testTag("earphone_opt_switch"),
          colors = SwitchDefaults.colors(
            checkedThumbColor = VipGold,
            checkedTrackColor = Color(0xFF6B4500),
            uncheckedThumbColor = VipTextMuted,
            uncheckedTrackColor = Color(0xFF2B1317)
          )
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Hardware FX items
    FxToggleItem(
      title = "Automatic Gain Control (AGC)",
      subtitle = if (isAgcAvailable) "Hardware dynamic volume leveling (Active)"
      else "Hardware AGC unavailable on this device",
      icon = Icons.Default.AutoAwesome,
      enabled = agcEnabled,
      onToggle = onAgcChange,
      isHardwareSupported = isAgcAvailable,
      testTag = "agc_switch"
    )

    FxToggleItem(
      title = "Noise Suppressor (NS)",
      subtitle = if (isNsAvailable) "Hardware ambient background noise filter"
      else "Hardware NS unavailable on this device",
      icon = Icons.Default.Hearing,
      enabled = nsEnabled,
      onToggle = onNsChange,
      isHardwareSupported = isNsAvailable,
      testTag = "ns_switch"
    )

    FxToggleItem(
      title = "Acoustic Echo Canceler (AEC)",
      subtitle = if (isAecAvailable) "Suppresses speaker feedback & acoustic reflections"
      else "Hardware AEC unavailable on this device",
      icon = Icons.Default.RecordVoiceOver,
      enabled = aecEnabled,
      onToggle = onAecChange,
      isHardwareSupported = isAecAvailable,
      testTag = "aec_switch"
    )

    FxToggleItem(
      title = "Vocal Clarity Presence (3.5 kHz)",
      subtitle = "Software DSP high-mid boost for speech intelligibility",
      icon = Icons.Default.Equalizer,
      enabled = clarityEnabled,
      onToggle = onClarityChange,
      isHardwareSupported = true,
      testTag = "clarity_switch"
    )

    FxToggleItem(
      title = "Low-Cut Rumble Filter (80 Hz)",
      subtitle = "High-pass filter removing desk bumps, breathing & wind pops",
      icon = Icons.Default.Mic,
      enabled = lowCutEnabled,
      onToggle = onLowCutChange,
      isHardwareSupported = true,
      testTag = "lowcut_switch"
    )
  }
}

@Composable
private fun FxToggleItem(
  title: String,
  subtitle: String,
  icon: ImageVector,
  enabled: Boolean,
  onToggle: (Boolean) -> Unit,
  isHardwareSupported: Boolean,
  testTag: String
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 5.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(
      modifier = Modifier.weight(1f),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Box(
        modifier = Modifier
          .size(28.dp)
          .clip(CircleShape)
          .background(Color(0xFF220C10)),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = icon,
          contentDescription = null,
          tint = if (enabled && isHardwareSupported) VipRedNeon else VipTextMuted,
          modifier = Modifier.size(15.dp)
        )
      }
      Spacer(modifier = Modifier.width(10.dp))
      Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isHardwareSupported) VipTextPrimary else VipTextMuted
          )
          if (!isHardwareSupported) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "(No HW)",
              fontSize = 9.sp,
              color = VipRedPrimary,
              fontWeight = FontWeight.Bold
            )
          }
        }
        Text(
          text = subtitle,
          fontSize = 10.sp,
          color = VipTextSecondary
        )
      }
    }

    Switch(
      checked = enabled && isHardwareSupported,
      onCheckedChange = { if (isHardwareSupported) onToggle(it) },
      enabled = isHardwareSupported,
      modifier = Modifier.testTag(testTag),
      colors = SwitchDefaults.colors(
        checkedThumbColor = VipRedNeon,
        checkedTrackColor = VipRedDark,
        uncheckedThumbColor = VipTextMuted,
        uncheckedTrackColor = Color(0xFF2B1317)
      )
    )
  }
}
