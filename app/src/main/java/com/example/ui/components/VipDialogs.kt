package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.MeterOrange
import com.example.ui.theme.VipBlack
import com.example.ui.theme.VipCardSurfaceElevated
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary

@Composable
fun FeedbackWarningDialog(
  onConfirmProceed: () -> Unit,
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VipCardSurfaceElevated,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(32.dp)
            .clip(CircleShape)
            .background(MeterOrange.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = MeterOrange,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Earphones Recommended",
          color = VipTextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column {
        Text(
          text = "No headphones or earphones were detected.",
          color = VipGold,
          fontSize = 13.sp,
          fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
          text = "Live monitoring plays your boosted microphone audio back in real-time. If played through the phone's built-in loudspeaker, the sound can re-enter the mic and create an acoustic feedback screech (howling loop).\n\nFor optimal experience, please plug in earphones or wired headphones.",
          color = VipTextSecondary,
          fontSize = 12.sp,
          lineHeight = 17.sp
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onConfirmProceed,
        modifier = Modifier.testTag("proceed_without_headphones_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = Color(0xFF4A1A22),
          contentColor = Color.White
        )
      ) {
        Text("Proceed with Caution", fontSize = 11.sp)
      }
    },
    dismissButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(
          containerColor = VipRedPrimary,
          contentColor = Color.White
        )
      ) {
        Text("I'll Plug In Earphones", fontSize = 11.sp, fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
fun HardwareNoticeDialog(
  onDismiss: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VipCardSurfaceElevated,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Info,
          contentDescription = null,
          tint = VipGold,
          modifier = Modifier.size(22.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
          text = "Hardware & Audio Notice",
          color = VipTextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
      ) {
        Text(
          text = "Important Transparency & Compatibility Information:",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = VipGold
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoSection(
          title = "1. Device Hardware Limits",
          content = "Microphone boosting is performed using genuine digital signal processing (DSP) amplification and native Android AudioFX APIs (AGC, Noise Suppressor, Echo Canceler). Boost levels are physically constrained by your smartphone's analog microphone capsule and internal ADC hardware."
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoSection(
          title = "2. Third-Party App Compatibility (WhatsApp, Calls)",
          content = "Android OS security architecture isolates third-party communication applications (such as WhatsApp, Skype, and cellular voice calls). For user privacy, standard Android does not allow background third-party apps to intercept or alter another app's active microphone stream without OS-level root modifications. OLD VIP Lucky Mic provides real-time amplification for recording, earphone monitoring, and supported audio sessions."
        )

        Spacer(modifier = Modifier.height(10.dp))

        InfoSection(
          title = "3. Earphone & Wired Headset Optimization",
          content = "Earphone microphones generally have lower sensitivity and are subject to clothing friction. This app includes specialized low-cut filtering (80 Hz) and 3.5 kHz vocal presence equalization to maximize speech clarity on earphones."
        )
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = VipRedPrimary)
      ) {
        Text("Understood", color = Color.White, fontWeight = FontWeight.Bold)
      }
    }
  )
}

@Composable
private fun InfoSection(title: String, content: String) {
  Box(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(8.dp))
      .background(VipBlack)
      .border(1.dp, Color(0xFF2E151A), RoundedCornerShape(8.dp))
      .padding(10.dp)
  ) {
    Column {
      Text(
        text = title,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = VipTextPrimary
      )
      Spacer(modifier = Modifier.height(3.dp))
      Text(
        text = content,
        fontSize = 11.sp,
        color = VipTextSecondary,
        lineHeight = 15.sp
      )
    }
  }
}
