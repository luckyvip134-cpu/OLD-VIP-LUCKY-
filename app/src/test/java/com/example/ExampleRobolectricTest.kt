package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.PresetManager
import com.example.model.MicPreset
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("OLD VIP Lucky Mic", appName)
  }

  @Test
  fun `preset manager loads built in presets`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = PresetManager(context)
    val presets = manager.getAllPresets()

    assertTrue("Presets should have at least 5 built-in presets", presets.size >= 5)
    val studio = presets.find { it.id == "preset_vip_studio" }
    assertNotNull(studio)
    assertEquals("VIP Studio Voice", studio?.name)
    assertTrue(studio?.isBuiltIn == true)
  }

  @Test
  fun `preset manager can save and delete custom presets`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val manager = PresetManager(context)

    val custom = MicPreset(
      id = "",
      name = "Test Gaming Boost",
      isBuiltIn = false,
      boostLevel = 3.0f,
      agcEnabled = true,
      noiseSuppressionEnabled = true,
      echoCancelerEnabled = true,
      clarityBoostEnabled = true,
      lowCutFilterEnabled = true,
      earphoneOptimized = true,
      description = "Custom test preset"
    )

    val saved = manager.saveCustomPreset(custom)
    assertNotNull(saved.id)
    assertTrue(saved.id.startsWith("custom_"))

    val allAfterSave = manager.getAllPresets()
    val found = allAfterSave.find { it.id == saved.id }
    assertNotNull(found)
    assertEquals("Test Gaming Boost", found?.name)
    assertEquals(3.0f, found?.boostLevel ?: 0f, 0.01f)

    // Delete custom preset
    val deleted = manager.deleteCustomPreset(saved.id)
    assertTrue(deleted)
    val allAfterDelete = manager.getAllPresets()
    assertFalse(allAfterDelete.any { it.id == saved.id })
  }
}
