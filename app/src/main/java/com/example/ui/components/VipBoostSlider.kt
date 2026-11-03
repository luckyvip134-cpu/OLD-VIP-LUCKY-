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
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.VipCardBorder
import com.example.ui.theme.VipCardBorderGlow
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldSubtle
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary
import kotlin.math.log10

@Composable
fun VipBoostSlider(
  boostLevel: Float,
  onBoostLevelChange: (Float) -> Unit,
  modifier: Modifier = Modifier
) {
  val boostPercentage = (boostLevel * 100).toInt()
  val boostDb = 20 * log10(boostLevel)
  val dbFormatted = if (boostDb <= 0.1f) "+0.0 dB" else "+%.1f dB".format(boostDb)

  val boostDescription = when {
    boostLevel >= 3.6f -> "VIP MAX BOOST (Ultra-sensitive)"
    boostLevel >= 2.5f -> "High Voice Sensitivity"
    boostLevel >= 1.8f -> "Studio Clear Presence"
    boostLevel >= 1.3f -> "Subtle Vocal Lift"
    else -> "Normal Unboosted (1:1)"
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(VipCardSurface)
      .border(1.dp, VipCardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp)
  ) {
    // Header
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
            imageVector = Icons.Default.Tune,
            contentDescription = null,
            tint = VipRedNeon,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "MICROPHONE BOOST LEVEL",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = VipTextPrimary,
            letterSpacing = 0.5.sp
          )
          Text(
            text = boostDescription,
            fontSize = 11.sp,
            color = if (boostLevel >= 3.5f) VipGold else VipTextSecondary,
            fontWeight = FontWeight.Medium
          )
        }
      }

      // Large Readout Pill
      Column(
        horizontalAlignment = Alignment.End
      ) {
        Text(
          text = "$boostPercentage%",
          fontSize = 20.sp,
          fontWeight = FontWeight.Black,
          color = VipRedNeon,
          fontFamily = FontFamily.Monospace
        )
        Text(
          text = dbFormatted,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          color = VipGold,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Boost Slider
    Slider(
      value = boostLevel,
      onValueChange = onBoostLevelChange,
      valueRange = 1.0f..4.0f,
      steps = 29, // 0.1 increments
      modifier = Modifier
        .testTag("boost_level_slider")
        .fillMaxWidth(),
      colors = SliderDefaults.colors(
        thumbColor = VipGold,
        activeTrackColor = VipRedNeon,
        inactiveTrackColor = Color(0xFF35171D),
        activeTickColor = VipGold.copy(alpha = 0.6f),
        inactiveTickColor = Color(0xFF4A1F26)
      )
    )

    // Quick Select Buttons
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val presets = listOf(
        1.0f to "1.0x Flat",
        1.8f to "1.8x Studio",
        2.5f to "2.5x High",
        4.0f to "4.0x MAX"
      )

      presets.forEach { (level, label) ->
        val isSelected = kotlin.math.abs(boostLevel - level) < 0.08f
        OutlinedButton(
          onClick = { onBoostLevelChange(level) },
          modifier = Modifier
            .weight(1f)
            .testTag("quick_boost_${level.toString().replace('.', '_')}"),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = if (isSelected) VipRedPrimary else Color(0xFF140B0E),
            contentColor = if (isSelected) Color.White else VipTextSecondary
          ),
          border = ButtonDefaults.outlinedButtonBorder.copy(
            brush = if (isSelected) Brush.horizontalGradient(listOf(VipGold, VipRedNeon))
            else Brush.horizontalGradient(listOf(VipCardBorder, VipCardBorder))
          ),
          contentPadding = androidx.compose.foundation.layout.PaddingValues(vertical = 6.dp)
        ) {
          Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            maxLines = 1
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // DSP Soft-Limiter & Protection Notice
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(8.dp))
        .background(Color(0xFF11080A))
        .border(1.dp, Color(0xFF281115), RoundedCornerShape(8.dp))
        .padding(horizontal = 10.dp, vertical = 7.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(
        imageVector = Icons.Default.Security,
        contentDescription = null,
        tint = VipGold,
        modifier = Modifier.size(14.dp)
      )
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = "Audio DSP uses soft-knee saturation limiting to prevent digital clipping & harsh distortion.",
        fontSize = 10.sp,
        color = VipTextMuted,
        lineHeight = 13.sp
      )
    }
  }
}
