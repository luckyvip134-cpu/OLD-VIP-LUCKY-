package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.filled.FiberManualRecord
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Science
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldSubtle
import com.example.ui.theme.VipRedDark
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary
import com.example.viewmodel.TestPlaybackMode

@Composable
fun VipTestStudioCard(
  isLiveMonitoring: Boolean,
  onToggleLiveMonitoring: () -> Unit,
  isRecordingTest: Boolean,
  hasTestRecording: Boolean,
  testPlaybackMode: TestPlaybackMode,
  testPlaybackProgress: Float,
  onStart5SecTest: () -> Unit,
  onCancelTestRecording: () -> Unit,
  onPlayTestRaw: () -> Unit,
  onPlayTestBoosted: () -> Unit,
  onStopTestPlayback: () -> Unit,
  isHeadsetConnected: Boolean,
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
    // Card Title
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
            imageVector = Icons.Default.Science,
            contentDescription = null,
            tint = VipGold,
            modifier = Modifier.size(18.dp)
          )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "MICROPHONE TEST STUDIO",
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold,
            color = VipTextPrimary,
            letterSpacing = 0.5.sp
          )
          Text(
            text = "Verify live boost & compare audio quality",
            fontSize = 11.sp,
            color = VipTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 1. Live Earphone Loopback Monitor Button
    OutlinedButton(
      onClick = onToggleLiveMonitoring,
      modifier = Modifier
        .testTag("live_monitor_button")
        .fillMaxWidth()
        .height(48.dp),
      shape = RoundedCornerShape(12.dp),
      colors = ButtonDefaults.outlinedButtonColors(
        containerColor = if (isLiveMonitoring) Color(0xFF360C12) else Color(0xFF14090C),
        contentColor = if (isLiveMonitoring) VipRedNeon else VipTextPrimary
      ),
      border = ButtonDefaults.outlinedButtonBorder.copy(
        brush = if (isLiveMonitoring) Brush.horizontalGradient(listOf(VipRedNeon, VipGold))
        else Brush.horizontalGradient(listOf(VipCardBorder, VipCardBorder))
      )
    ) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Headphones,
            contentDescription = null,
            tint = if (isLiveMonitoring) VipRedNeon else VipGold,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(10.dp))
          Text(
            text = if (isLiveMonitoring) "Live Earphone Monitor: ON" else "Live Monitor (Hear in Earphones)",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
        Box(
          modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(if (isLiveMonitoring) VipRedPrimary else Color(0xFF261216))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = if (isLiveMonitoring) "LISTENING" else "OFF",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (isLiveMonitoring) Color.White else VipTextMuted
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(12.dp))

    // 2. 5-Second Test Recording Box
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(12.dp))
        .background(Color(0xFF12080A))
        .border(1.dp, Color(0xFF2E1317), RoundedCornerShape(12.dp))
        .padding(12.dp)
    ) {
      Text(
        text = "5-Second Mic Record & A/B Comparison",
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        color = VipTextPrimary
      )
      Text(
        text = "Record a sample to hear the direct difference before & after boost.",
        fontSize = 11.sp,
        color = VipTextSecondary
      )

      Spacer(modifier = Modifier.height(10.dp))

      if (!isRecordingTest && !hasTestRecording) {
        // Initial Record Button
        Button(
          onClick = onStart5SecTest,
          modifier = Modifier
            .testTag("start_test_record_button")
            .fillMaxWidth()
            .height(44.dp),
          shape = RoundedCornerShape(10.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = VipRedPrimary,
            contentColor = Color.White
          )
        ) {
          Icon(
            imageVector = Icons.Default.FiberManualRecord,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Record 5-Second Test Sample",
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      } else if (isRecordingTest) {
        // Recording In Progress State
        Column(
          modifier = Modifier.fillMaxWidth(),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(10.dp)
                  .clip(CircleShape)
                  .background(VipRedNeon)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Recording 5s Sample...",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = VipRedNeon
              )
            }
            OutlinedButton(
              onClick = onCancelTestRecording,
              modifier = Modifier.height(32.dp),
              shape = RoundedCornerShape(6.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Text("Cancel", fontSize = 10.sp, color = VipTextMuted)
            }
          }
          Spacer(modifier = Modifier.height(8.dp))
          LinearProgressIndicator(
            modifier = Modifier
              .fillMaxWidth()
              .height(6.dp)
              .clip(RoundedCornerShape(3.dp)),
            color = VipRedNeon,
            trackColor = Color(0xFF2E1317)
          )
        }
      } else {
        // Recorded sample ready: A/B Comparison Controls
        Column(modifier = Modifier.fillMaxWidth()) {
          // Playback progress indicator
          if (testPlaybackMode != TestPlaybackMode.IDLE) {
            LinearProgressIndicator(
              progress = { testPlaybackProgress },
              modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
              color = if (testPlaybackMode == TestPlaybackMode.PLAYING_BOOSTED) VipGold else Color.White,
              trackColor = Color(0xFF2E1317)
            )
            Spacer(modifier = Modifier.height(8.dp))
          }

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            // Play Raw (Unboosted)
            Button(
              onClick = {
                if (testPlaybackMode == TestPlaybackMode.PLAYING_RAW) onStopTestPlayback()
                else onPlayTestRaw()
              },
              modifier = Modifier
                .weight(1f)
                .testTag("play_raw_test_button")
                .height(42.dp),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (testPlaybackMode == TestPlaybackMode.PLAYING_RAW) Color(0xFF452429) else Color(0xFF201317),
                contentColor = Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(
                1.dp,
                if (testPlaybackMode == TestPlaybackMode.PLAYING_RAW) VipRedNeon else VipCardBorder
              )
            ) {
              Icon(
                imageVector = if (testPlaybackMode == TestPlaybackMode.PLAYING_RAW) Icons.Default.Stop else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("Raw Mic", fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            }

            // Play Boosted (With active settings)
            Button(
              onClick = {
                if (testPlaybackMode == TestPlaybackMode.PLAYING_BOOSTED) onStopTestPlayback()
                else onPlayTestBoosted()
              },
              modifier = Modifier
                .weight(1.3f)
                .testTag("play_boosted_test_button")
                .height(42.dp),
              shape = RoundedCornerShape(8.dp),
              colors = ButtonDefaults.buttonColors(
                containerColor = if (testPlaybackMode == TestPlaybackMode.PLAYING_BOOSTED) VipRedPrimary else Color(0xFF8B0D15),
                contentColor = Color.White
              ),
              border = androidx.compose.foundation.BorderStroke(1.dp, VipGold)
            ) {
              Icon(
                imageVector = if (testPlaybackMode == TestPlaybackMode.PLAYING_BOOSTED) Icons.Default.Stop else Icons.Default.VolumeUp,
                contentDescription = null,
                tint = VipGold,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text("VIP Boosted", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            // Re-record Button
            OutlinedButton(
              onClick = onStart5SecTest,
              modifier = Modifier
                .testTag("rerecord_test_button")
                .height(42.dp),
              shape = RoundedCornerShape(8.dp),
              contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp)
            ) {
              Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Re-record sample",
                tint = VipTextSecondary,
                modifier = Modifier.size(18.dp)
              )
            }
          }
        }
      }
    }
  }
}
