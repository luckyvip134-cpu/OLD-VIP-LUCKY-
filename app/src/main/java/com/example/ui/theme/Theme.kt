package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val VipColorScheme = darkColorScheme(
  primary = VipRedNeon,
  onPrimary = Color.White,
  primaryContainer = VipRedDark,
  onPrimaryContainer = Color.White,
  secondary = VipGold,
  onSecondary = Color.Black,
  secondaryContainer = VipCardSurfaceElevated,
  onSecondaryContainer = VipGold,
  tertiary = VipRedPrimary,
  onTertiary = Color.White,
  background = VipBlack,
  onBackground = VipTextPrimary,
  surface = VipDarkSurface,
  onSurface = VipTextPrimary,
  surfaceVariant = VipCardSurface,
  onSurfaceVariant = VipTextSecondary,
  outline = VipCardBorder,
  outlineVariant = VipCardBorderGlow
)

@Composable
fun MyApplicationTheme(
  content: @Composable () -> Unit
) {
  MaterialTheme(
    colorScheme = VipColorScheme,
    typography = Typography,
    content = content
  )
}
