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
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MeterGreen
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

@Composable
fun VipHeader(
  activePresetName: String,
  isHeadsetConnected: Boolean,
  headsetName: String,
  onOpenPresets: () -> Unit,
  onOpenInfo: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp))
      .background(
        Brush.verticalGradient(
          colors = listOf(
            Color(0xFF22080C),
            Color(0xFF14070A),
            Color(0xFF0C0506)
          )
        )
      )
      .border(
        width = 1.dp,
        brush = Brush.verticalGradient(
          colors = listOf(VipCardBorderGlow, VipCardBorder)
        ),
        shape = RoundedCornerShape(bottomStart = 20.dp, bottomEnd = 20.dp)
      )
      .padding(horizontal = 16.dp, vertical = 14.dp)
  ) {
    Column {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertornally()
      ) {
        // App title with VIP Crest
        Row(
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(42.dp)
              .clip(CircleShape)
              .background(
                Brush.radialGradient(
                  listOf(VipRedNeon, VipRedPrimary, Color(0xFF4A0008))
                )
              )
              .border(1.5.dp, VipGold, CircleShape),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.Mic,
              contentDescription = "VIP Mic Emblem",
              tint = VipGold,
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "OLD VIP",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = VipGold,
                letterSpacing = 1.2.sp
              )
              Spacer(modifier = Modifier.width(6.dp))
              Box(
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(VipRedPrimary)
                  .padding(horizontal = 5.dp, vertical = 1.dp)
              ) {
                Text(
                  text = "LUCKY MIC",
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  color = Color.White,
                  letterSpacing = 1.sp
                )
              }
            }
            Text(
              text = "PRO AUDIO VOLUME BOOSTER",
              fontSize = 9.sp,
              fontWeight = FontWeight.Medium,
              color = VipTextSecondary,
              letterSpacing = 0.8.sp
            )
          }
        }

        // Action Buttons
        Row(verticalAlignment = Alignment.CenterVertically) {
          IconButton(
            onClick = onOpenPresets,
            modifier = Modifier
              .testTag("open_presets_button")
              .size(42.dp)
              .clip(CircleShape)
              .background(VipCardSurface)
              .border(1.dp, VipCardBorder, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Bookmark,
              contentDescription = "Sound Presets",
              tint = VipGold,
              modifier = Modifier.size(20.dp)
            )
          }

          Spacer(modifier = Modifier.width(8.dp))

          IconButton(
            onClick = onOpenInfo,
            modifier = Modifier
              .testTag("open_info_button")
              .size(42.dp)
              .clip(CircleShape)
              .background(VipCardSurface)
              .border(1.dp, VipCardBorder, CircleShape)
          ) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = "Hardware Info & Notice",
              tint = VipTextSecondary,
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Status pill bar: Active preset & Headset status
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Preset Pill
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(VipGoldSubtle)
            .border(1.dp, VipGold.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            tint = VipGold,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = activePresetName,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = VipGold
          )
        }

        // Headset detection status
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(
              if (isHeadsetConnected) MeterGreen.copy(alpha = 0.15f)
              else Color(0x22FFFFFF)
            )
            .border(
              1.dp,
              if (isHeadsetConnected) MeterGreen.copy(alpha = 0.4f)
              else Color(0x33FFFFFF),
              RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 9.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = if (isHeadsetConnected) MeterGreen else VipTextMuted,
            modifier = Modifier.size(13.dp)
          )
          Spacer(modifier = Modifier.width(5.dp))
          Text(
            text = if (isHeadsetConnected) "Headset Optimized" else "Phone Mic",
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = if (isHeadsetConnected) MeterGreen else VipTextSecondary
          )
        }
      }
    }
  }
}

private fun Alignment.Companion.CenterVertornally(): Alignment.Vertical = Alignment.CenterVertically
