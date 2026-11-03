package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.screens.VipMicScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.VipMicViewModel

class MainActivity : ComponentActivity() {
  private val viewModel: VipMicViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        VipMicScreen(viewModel = viewModel)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    viewModel.refreshHeadsetStatus()
  }
}
