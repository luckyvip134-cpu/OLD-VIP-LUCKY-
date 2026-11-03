package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MeterClip
import com.example.ui.theme.MeterGreen
import com.example.ui.theme.MeterOrange
import com.example.ui.theme.MeterRed
import com.example.ui.theme.MeterYellow
import com.example.ui.theme.VipBlack
import com.example.ui.theme.VipCardBorder
import com.example.ui.theme.VipCardBorderGlow
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipRedDark
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary
import kotlin.math.sin

@Composable
fun VipMeterSection(
  isMasterOn: Boolean,
  inputLevelDb: Float,
  peakLevel: Float,
  waveformPoints: List<Float>,
  onToggleMasterPower: () -> Unit,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "pulse")
  val pulseScale by infiniteTransition.animateFloat(
    initialValue = 1f,
    targetValue = if (isMasterOn) 1.06f else 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = FastOutSlowInEasing),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulseScale"
  )

  val buttonGlowColor by animateColorAsState(
    targetValue = if (isMasterOn) VipRedNeon else Color(0xFF3A1E22),
    label = "buttonGlow"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(VipCardSurface)
      .border(1.dp, VipCardBorder, RoundedCornerShape(18.dp))
      .padding(16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top row: Power Button + Status Label
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = if (isMasterOn) "VIP ENGINE ACTIVE" else "VIP ENGINE STANDBY",
          fontSize = 14.sp,
          fontWeight = FontWeight.Bold,
          color = if (isMasterOn) VipRedNeon else VipTextMuted,
          letterSpacing = 0.5.sp
        )
        Text(
          text = if (isMasterOn) "Live amplification & DSP active" else "Tap power button to enable mic",
          fontSize = 11.sp,
          color = VipTextSecondary
        )
      }

      // Master Power ON/OFF Button
      Box(
        modifier = Modifier
          .testTag("master_power_button")
          .size(64.dp)
          .scale(pulseScale)
          .shadow(
            elevation = if (isMasterOn) 16.dp else 4.dp,
            shape = CircleShape,
            ambientColor = buttonGlowColor,
            spotColor = buttonGlowColor
          )
          .clip(CircleShape)
          .background(
            Brush.radialGradient(
              listOf(
                if (isMasterOn) VipRedNeon else Color(0xFF452429),
                if (isMasterOn) VipRedPrimary else Color(0xFF261215),
                if (isMasterOn) VipRedDark else Color(0xFF140A0C)
              )
            )
          )
          .border(
            width = 2.dp,
            brush = Brush.sweepGradient(
              listOf(
                if (isMasterOn) VipGold else Color(0xFF553338),
                if (isMasterOn) VipRedNeon else Color(0xFF331A1D),
                if (isMasterOn) VipGold else Color(0xFF553338)
              )
            ),
            shape = CircleShape
          )
          .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = ripple(bounded = true, radius = 32.dp),
            onClick = onToggleMasterPower
          ),
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.PowerSettingsNew,
          contentDescription = "Master Microphone Power Toggle",
          tint = if (isMasterOn) Color.White else VipTextMuted,
          modifier = Modifier.size(32.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Real-Time VU Meter Bar
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(VipBlack)
        .border(1.dp, Color(0xFF2E171B), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = if (isMasterOn) Icons.Default.Mic else Icons.Default.MicOff,
            contentDescription = null,
            tint = if (isMasterOn) VipRedNeon else VipTextMuted,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "INPUT LEVEL (VU)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = VipTextPrimary,
            letterSpacing = 0.5.sp
          )
        }

        // Numerical dB readout
        val dbDisplay = if (!isMasterOn) "OFF" else if (inputLevelDb <= -59.5f) "-INF dB" else "%.1f dB".format(inputLevelDb)
        Text(
          text = dbDisplay,
          fontFamily = FontFamily.Monospace,
          fontSize = 13.sp,
          fontWeight = FontWeight.Bold,
          color = when {
            !isMasterOn -> VipTextMuted
            inputLevelDb > -3f -> MeterClip
            inputLevelDb > -9f -> MeterOrange
            inputLevelDb > -24f -> MeterYellow
            else -> MeterGreen
          }
        )
      }

      Spacer(modifier = Modifier.height(8.dp))

      // Multi-Segment LED meter bar
      SegmentedLedBar(
        levelDb = if (isMasterOn) inputLevelDb else -60f,
        peakRatio = if (isMasterOn) peakLevel else 0f,
        modifier = Modifier
          .fillMaxWidth()
          .height(18.dp)
      )

      Spacer(modifier = Modifier.height(6.dp))

      // dB scale markings
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
      ) {
        Text("-60", fontSize = 9.sp, color = VipTextMuted, fontFamily = FontFamily.Monospace)
        Text("-40", fontSize = 9.sp, color = VipTextMuted, fontFamily = FontFamily.Monospace)
        Text("-20", fontSize = 9.sp, color = MeterGreen, fontFamily = FontFamily.Monospace)
        Text("-12", fontSize = 9.sp, color = MeterYellow, fontFamily = FontFamily.Monospace)
        Text("-6", fontSize = 9.sp, color = MeterOrange, fontFamily = FontFamily.Monospace)
        Text("0 dB", fontSize = 9.sp, color = MeterRed, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // Waveform Scope Canvas
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(58.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(0xFF070405))
        .border(1.dp, Color(0xFF281316), RoundedCornerShape(10.dp))
        .padding(4.dp)
    ) {
      Canvas(
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
      ) {
        val width = size.width
        val height = size.height
        val midY = height / 2f

        // Draw subtle center line
        drawLine(
          color = Color(0x33FF2A3B),
          start = Offset(0f, midY),
          end = Offset(width, midY),
          strokeWidth = 1f
        )

        if (!isMasterOn || waveformPoints.isEmpty()) {
          // Draw subtle idle sine wave
          val idlePath = Path()
          idlePath.moveTo(0f, midY)
          for (x in 0..width.toInt() step 6) {
            val y = midY + (sin(x * 0.05) * 2f).toFloat()
            idlePath.lineTo(x.toFloat(), y)
          }
          drawPath(
            path = idlePath,
            color = Color(0x22FFFFFF),
            style = Stroke(width = 1.5f)
          )
          return@Canvas
        }

        // Draw live audio waveform
        val path = Path()
        val stepX = width / (waveformPoints.size - 1).coerceAtLeast(1)

        waveformPoints.forEachIndexed { index, amp ->
          val x = index * stepX
          val y = midY - (amp * (height * 0.44f))
          if (index == 0) {
            path.moveTo(x, y)
          } else {
            path.lineTo(x, y)
          }
        }

        // Draw neon glow under-trace
        drawPath(
          path = path,
          color = VipRedPrimary.copy(alpha = 0.35f),
          style = Stroke(width = 4f, cap = StrokeCap.Round)
        )
        // Draw crisp core trace
        drawPath(
          path = path,
          brush = Brush.horizontalGradient(
            listOf(VipRedNeon, VipGold, VipRedNeon)
          ),
          style = Stroke(width = 2f, cap = StrokeCap.Round)
        )
      }
    }
  }
}

@Composable
fun SegmentedLedBar(
  levelDb: Float,
  peakRatio: Float,
  modifier: Modifier = Modifier
) {
  // Normalize dB from -60..0 to 0..1
  val normalized = ((levelDb + 60f) / 60f).coerceIn(0f, 1f)
  val segmentCount = 28

  Canvas(modifier = modifier) {
    val totalWidth = size.width
    val segHeight = size.height
    val spacing = 3.dp.toPx()
    val totalSpacing = spacing * (segmentCount - 1)
    val segWidth = (totalWidth - totalSpacing) / segmentCount

    val litCount = (normalized * segmentCount).toInt()

    for (i in 0 until segmentCount) {
      val left = i * (segWidth + spacing)
      val fraction = i.toFloat() / segmentCount

      val baseColor = when {
        fraction < 0.55f -> MeterGreen
        fraction < 0.75f -> MeterYellow
        fraction < 0.88f -> MeterOrange
        else -> MeterRed
      }

      val isLit = i < litCount
      val isPeak = (peakRatio * segmentCount).toInt() == i && isLit

      val color = when {
        isPeak -> Color.White
        isLit -> baseColor
        else -> baseColor.copy(alpha = 0.12f)
      }

      drawRoundRect(
        color = color,
        topLeft = Offset(left, 0f),
        size = Size(segWidth, segHeight),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(2.dp.toPx(), 2.dp.toPx())
      )
    }
  }
}
