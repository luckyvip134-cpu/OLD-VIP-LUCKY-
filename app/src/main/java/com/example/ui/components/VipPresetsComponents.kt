package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.MicPreset
import com.example.ui.theme.VipBlack
import com.example.ui.theme.VipCardBorder
import com.example.ui.theme.VipCardBorderGlow
import com.example.ui.theme.VipCardSurface
import com.example.ui.theme.VipCardSurfaceElevated
import com.example.ui.theme.VipGold
import com.example.ui.theme.VipGoldDark
import com.example.ui.theme.VipGoldSubtle
import com.example.ui.theme.VipRedDark
import com.example.ui.theme.VipRedNeon
import com.example.ui.theme.VipRedPrimary
import com.example.ui.theme.VipTextMuted
import com.example.ui.theme.VipTextPrimary
import com.example.ui.theme.VipTextSecondary

@Composable
fun VipPresetsQuickBar(
  presets: List<MicPreset>,
  activePreset: MicPreset?,
  onSelectPreset: (MicPreset) -> Unit,
  onOpenSavePresetDialog: () -> Unit,
  onOpenManagePresetsDialog: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(18.dp))
      .background(VipCardSurface)
      .border(1.dp, VipCardBorder, RoundedCornerShape(18.dp))
      .padding(vertical = 12.dp, horizontal = 14.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = VipGold,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "SOUND PRESETS",
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          color = VipTextPrimary,
          letterSpacing = 0.5.sp
        )
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Save current preset button
        TextButton(
          onClick = onOpenSavePresetDialog,
          modifier = Modifier.testTag("save_preset_header_button")
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            tint = VipGold,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Save Current",
            fontSize = 11.sp,
            color = VipGold,
            fontWeight = FontWeight.SemiBold
          )
        }

        // Manage Presets Button
        TextButton(
          onClick = onOpenManagePresetsDialog,
          modifier = Modifier.testTag("manage_presets_header_button")
        ) {
          Icon(
            imageVector = Icons.Default.Settings,
            contentDescription = null,
            tint = VipTextSecondary,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "Manage (${presets.size})",
            fontSize = 11.sp,
            color = VipTextSecondary
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Horizontal Scrollable Chips
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(rememberScrollState()),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      presets.forEach { preset ->
        val isSelected = activePreset?.id == preset.id
        Box(
          modifier = Modifier
            .testTag("preset_chip_${preset.id}")
            .clip(RoundedCornerShape(10.dp))
            .background(
              if (isSelected) Brush.horizontalGradient(listOf(VipRedDark, Color(0xFF4A0A12)))
              else Brush.horizontalGradient(listOf(Color(0xFF190F12), Color(0xFF140B0E)))
            )
            .border(
              1.dp,
              if (isSelected) VipGold else VipCardBorder,
              RoundedCornerShape(10.dp)
            )
            .clickable { onSelectPreset(preset) }
            .padding(horizontal = 12.dp, vertical = 7.dp)
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            if (preset.isBuiltIn) {
              Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = if (isSelected) VipGold else VipTextMuted,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(5.dp))
            }
            Column {
              Text(
                text = preset.name,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else VipTextSecondary
              )
              Text(
                text = "${preset.boostPercentage}% Boost",
                fontSize = 9.sp,
                color = if (isSelected) VipGold else VipTextMuted
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun SavePresetDialog(
  currentBoostLevel: Float,
  agcEnabled: Boolean,
  nsEnabled: Boolean,
  aecEnabled: Boolean,
  clarityEnabled: Boolean,
  lowCutEnabled: Boolean,
  onSave: (name: String, description: String) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var errorText by remember { mutableStateOf<String?>(null) }

  val boostPercentage = (currentBoostLevel * 100).toInt()

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VipCardSurfaceElevated,
    title = {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Bookmark,
          contentDescription = null,
          tint = VipGold,
          modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "Save Sound Preset",
          color = VipTextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      }
    },
    text = {
      Column {
        Text(
          text = "Save your current microphone boost ($boostPercentage%) and audio enhancement settings for instant recall.",
          fontSize = 12.sp,
          color = VipTextSecondary
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
          value = name,
          onValueChange = {
            name = it
            if (it.isNotBlank()) errorText = null
          },
          label = { Text("Preset Name (e.g., My Gaming Mic)") },
          singleLine = true,
          modifier = Modifier
            .testTag("preset_name_input")
            .fillMaxWidth(),
          isError = errorText != null,
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VipGold,
            unfocusedBorderColor = VipCardBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = VipGold,
            unfocusedLabelColor = VipTextSecondary
          )
        )

        if (errorText != null) {
          Text(
            text = errorText ?: "",
            color = VipRedNeon,
            fontSize = 11.sp,
            modifier = Modifier.padding(top = 4.dp)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Notes / Description (Optional)") },
          singleLine = true,
          modifier = Modifier
            .testTag("preset_desc_input")
            .fillMaxWidth(),
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = VipGold,
            unfocusedBorderColor = VipCardBorder,
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedLabelColor = VipGold,
            unfocusedLabelColor = VipTextSecondary
          )
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Preset Settings Summary
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(VipBlack)
            .border(1.dp, Color(0xFF33151A), RoundedCornerShape(8.dp))
            .padding(10.dp)
        ) {
          Column {
            Text(
              text = "INCLUDED SETTINGS:",
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              color = VipGold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• Boost Gain: $boostPercentage% (+${"%.1f".format(20 * kotlin.math.log10(currentBoostLevel))} dB)\n" +
                  "• AGC: ${if (agcEnabled) "ON" else "OFF"}  |  Noise Suppr: ${if (nsEnabled) "ON" else "OFF"}\n" +
                  "• Echo Cancel: ${if (aecEnabled) "ON" else "OFF"}  |  Clarity: ${if (clarityEnabled) "ON" else "OFF"}\n" +
                  "• Low-Cut Filter: ${if (lowCutEnabled) "ON" else "OFF"}",
              fontSize = 10.sp,
              color = VipTextSecondary,
              lineHeight = 15.sp
            )
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          if (name.isBlank()) {
            errorText = "Please enter a preset name"
          } else {
            onSave(name.trim(), description.trim())
          }
        },
        modifier = Modifier.testTag("confirm_save_preset_button"),
        colors = ButtonDefaults.buttonColors(
          containerColor = VipRedPrimary,
          contentColor = Color.White
        )
      ) {
        Text("Save Preset", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = VipTextMuted)
      }
    }
  )
}

@Composable
fun ManagePresetsDialog(
  presets: List<MicPreset>,
  activePresetId: String?,
  onSelectPreset: (MicPreset) -> Unit,
  onDeletePreset: (String) -> Unit,
  onOpenSaveNew: () -> Unit,
  onDismiss: () -> Unit
) {
  var presetToDelete by remember { mutableStateOf<MicPreset?>(null) }

  AlertDialog(
    onDismissRequest = onDismiss,
    containerColor = VipCardSurfaceElevated,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Bookmark,
            contentDescription = null,
            tint = VipGold,
            modifier = Modifier.size(20.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "Manage Sound Presets",
            color = VipTextPrimary,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .heightIn(max = 420.dp)
      ) {
        // Quick button to save current settings
        OutlinedButton(
          onClick = {
            onDismiss()
            onOpenSaveNew()
          },
          modifier = Modifier
            .testTag("save_new_from_manage_button")
            .fillMaxWidth(),
          shape = RoundedCornerShape(8.dp),
          colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFF2A141A),
            contentColor = VipGold
          ),
          border = androidx.compose.foundation.BorderStroke(1.dp, VipGoldDark)
        ) {
          Icon(
            imageVector = Icons.Default.Add,
            contentDescription = null,
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text("Save Current Mic Settings as New Preset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(12.dp))

        LazyColumn(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          items(presets, key = { it.id }) { preset ->
            val isSelected = preset.id == activePresetId
            Box(
              modifier = Modifier
                .testTag("manage_preset_item_${preset.id}")
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(
                  if (isSelected) Color(0xFF330E14) else VipBlack
                )
                .border(
                  1.dp,
                  if (isSelected) VipGold else Color(0xFF30151A),
                  RoundedCornerShape(10.dp)
                )
                .padding(10.dp)
            ) {
              Column {
                Row(
                  modifier = Modifier.fillMaxWidth(),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                  ) {
                    Text(
                      text = preset.name,
                      fontSize = 13.sp,
                      fontWeight = FontWeight.Bold,
                      color = if (isSelected) VipGold else VipTextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                          if (preset.isBuiltIn) VipGoldSubtle else Color(0xFF28181C)
                        )
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                    ) {
                      Text(
                        text = if (preset.isBuiltIn) "VIP BUILT-IN" else "CUSTOM",
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (preset.isBuiltIn) VipGold else VipTextSecondary
                      )
                    }
                  }

                  Row(verticalAlignment = Alignment.CenterVertically) {
                    // Load / Apply Button
                    Button(
                      onClick = { onSelectPreset(preset) },
                      modifier = Modifier
                        .testTag("load_preset_btn_${preset.id}")
                        .height(30.dp),
                      shape = RoundedCornerShape(6.dp),
                      colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelected) VipRedPrimary else Color(0xFF2B1317),
                        contentColor = Color.White
                      ),
                      contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp)
                    ) {
                      if (isSelected) {
                        Icon(
                          imageVector = Icons.Default.Check,
                          contentDescription = null,
                          modifier = Modifier.size(12.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                      }
                      Text(
                        text = if (isSelected) "Active" else "Load",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                      )
                    }

                    // Delete Custom Preset Button
                    if (!preset.isBuiltIn) {
                      Spacer(modifier = Modifier.width(4.dp))
                      IconButton(
                        onClick = { presetToDelete = preset },
                        modifier = Modifier
                          .testTag("delete_preset_btn_${preset.id}")
                          .size(30.dp)
                      ) {
                        Icon(
                          imageVector = Icons.Default.Delete,
                          contentDescription = "Delete preset",
                          tint = VipRedNeon,
                          modifier = Modifier.size(16.dp)
                        )
                      }
                    }
                  }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                  text = preset.description.ifBlank { "Custom preset with ${preset.boostPercentage}% boost" },
                  fontSize = 10.sp,
                  color = VipTextSecondary
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Detail badges
                Row(
                  horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                  DetailTag(text = "${preset.boostPercentage}% Boost")
                  if (preset.agcEnabled) DetailTag(text = "AGC")
                  if (preset.noiseSuppressionEnabled) DetailTag(text = "NS")
                  if (preset.echoCancelerEnabled) DetailTag(text = "AEC")
                  if (preset.clarityBoostEnabled) DetailTag(text = "Clarity")
                }
              }
            }
          }
        }
      }
    },
    confirmButton = {
      Button(
        onClick = onDismiss,
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF29151A))
      ) {
        Text("Close", color = Color.White)
      }
    }
  )

  // Confirm delete sub-dialog
  if (presetToDelete != null) {
    AlertDialog(
      onDismissRequest = { presetToDelete = null },
      containerColor = VipCardSurfaceElevated,
      title = { Text("Delete Preset?", color = Color.White) },
      text = {
        Text(
          "Are you sure you want to delete the preset \"${presetToDelete?.name}\"?",
          color = VipTextSecondary
        )
      },
      confirmButton = {
        Button(
          onClick = {
            presetToDelete?.let { onDeletePreset(it.id) }
            presetToDelete = null
          },
          colors = ButtonDefaults.buttonColors(containerColor = VipRedPrimary)
        ) {
          Text("Delete", color = Color.White)
        }
      },
      dismissButton = {
        TextButton(onClick = { presetToDelete = null }) {
          Text("Cancel", color = VipTextMuted)
        }
      }
    )
  }
}

@Composable
private fun DetailTag(text: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(3.dp))
      .background(Color(0xFF1E1013))
      .padding(horizontal = 4.dp, vertical = 1.dp)
  ) {
    Text(
      text = text,
      fontSize = 8.sp,
      color = VipTextSecondary,
      fontWeight = FontWeight.Medium
    )
  }
}
