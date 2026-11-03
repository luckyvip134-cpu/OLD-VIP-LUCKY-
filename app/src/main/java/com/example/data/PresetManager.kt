package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.MicPreset
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class PresetManager(context: Context) {
  private val prefs: SharedPreferences =
    context.getSharedPreferences("vip_mic_presets", Context.MODE_PRIVATE)

  companion object {
    private const val KEY_CUSTOM_PRESETS = "custom_presets_json"
    private const val KEY_SELECTED_PRESET_ID = "selected_preset_id"

    val BUILT_IN_PRESETS = listOf(
      MicPreset(
        id = "preset_vip_studio",
        name = "VIP Studio Voice",
        isBuiltIn = true,
        boostLevel = 2.2f,
        agcEnabled = true,
        noiseSuppressionEnabled = true,
        echoCancelerEnabled = true,
        clarityBoostEnabled = true,
        lowCutFilterEnabled = true,
        earphoneOptimized = true,
        description = "Balanced luxury studio warmth and crisp vocal clarity"
      ),
      MicPreset(
        id = "preset_super_earphone",
        name = "Super Earphone Boost",
        isBuiltIn = true,
        boostLevel = 3.6f,
        agcEnabled = true,
        noiseSuppressionEnabled = true,
        echoCancelerEnabled = false,
        clarityBoostEnabled = true,
        lowCutFilterEnabled = true,
        earphoneOptimized = true,
        description = "Maximum amplification tuned for quiet wired earphone microphones"
      ),
      MicPreset(
        id = "preset_gaming_chat",
        name = "Gaming Live Chat",
        isBuiltIn = true,
        boostLevel = 2.0f,
        agcEnabled = true,
        noiseSuppressionEnabled = true,
        echoCancelerEnabled = true,
        clarityBoostEnabled = true,
        lowCutFilterEnabled = true,
        earphoneOptimized = true,
        description = "Clear squad voice with ambient noise suppression"
      ),
      MicPreset(
        id = "preset_asmr_whisper",
        name = "ASMR Whisper Boost",
        isBuiltIn = true,
        boostLevel = 4.0f,
        agcEnabled = false,
        noiseSuppressionEnabled = false,
        echoCancelerEnabled = false,
        clarityBoostEnabled = true,
        lowCutFilterEnabled = false,
        earphoneOptimized = false,
        description = "Ultra high sensitivity capture for quiet whisper audio"
      ),
      MicPreset(
        id = "preset_clean_podcast",
        name = "Clean Natural Mic",
        isBuiltIn = true,
        boostLevel = 1.4f,
        agcEnabled = true,
        noiseSuppressionEnabled = true,
        echoCancelerEnabled = true,
        clarityBoostEnabled = false,
        lowCutFilterEnabled = true,
        earphoneOptimized = false,
        description = "Subtle transparent volume boost with acoustic echo suppression"
      )
    )
  }

  fun getAllPresets(): List<MicPreset> {
    val customList = getCustomPresets()
    return BUILT_IN_PRESETS + customList
  }

  fun getCustomPresets(): List<MicPreset> {
    val jsonString = prefs.getString(KEY_CUSTOM_PRESETS, null) ?: return emptyList()
    val list = mutableListOf<MicPreset>()
    try {
      val jsonArray = JSONArray(jsonString)
      for (i in 0 until jsonArray.length()) {
        val obj = jsonArray.getJSONObject(i)
        list.add(
          MicPreset(
            id = obj.getString("id"),
            name = obj.getString("name"),
            isBuiltIn = false,
            boostLevel = obj.optDouble("boostLevel", 2.0).toFloat(),
            agcEnabled = obj.optBoolean("agcEnabled", true),
            noiseSuppressionEnabled = obj.optBoolean("noiseSuppressionEnabled", true),
            echoCancelerEnabled = obj.optBoolean("echoCancelerEnabled", true),
            clarityBoostEnabled = obj.optBoolean("clarityBoostEnabled", true),
            lowCutFilterEnabled = obj.optBoolean("lowCutFilterEnabled", true),
            earphoneOptimized = obj.optBoolean("earphoneOptimized", true),
            description = obj.optString("description", "Custom user preset")
          )
        )
      }
    } catch (_: Exception) {
      // Ignore corrupt json
    }
    return list
  }

  fun saveCustomPreset(preset: MicPreset): MicPreset {
    val currentCustom = getCustomPresets().toMutableList()
    val finalPreset = if (preset.id.isBlank() || preset.isBuiltIn) {
      preset.copy(id = "custom_" + UUID.randomUUID().toString(), isBuiltIn = false)
    } else {
      preset.copy(isBuiltIn = false)
    }

    val existingIndex = currentCustom.indexOfFirst { it.id == finalPreset.id }
    if (existingIndex >= 0) {
      currentCustom[existingIndex] = finalPreset
    } else {
      currentCustom.add(finalPreset)
    }

    saveCustomPresetsList(currentCustom)
    saveSelectedPresetId(finalPreset.id)
    return finalPreset
  }

  fun deleteCustomPreset(id: String): Boolean {
    val currentCustom = getCustomPresets().toMutableList()
    val removed = currentCustom.removeAll { it.id == id }
    if (removed) {
      saveCustomPresetsList(currentCustom)
      if (getSelectedPresetId() == id) {
        saveSelectedPresetId(BUILT_IN_PRESETS.first().id)
      }
    }
    return removed
  }

  private fun saveCustomPresetsList(list: List<MicPreset>) {
    val jsonArray = JSONArray()
    for (preset in list) {
      val obj = JSONObject().apply {
        put("id", preset.id)
        put("name", preset.name)
        put("boostLevel", preset.boostLevel.toDouble())
        put("agcEnabled", preset.agcEnabled)
        put("noiseSuppressionEnabled", preset.noiseSuppressionEnabled)
        put("echoCancelerEnabled", preset.echoCancelerEnabled)
        put("clarityBoostEnabled", preset.clarityBoostEnabled)
        put("lowCutFilterEnabled", preset.lowCutFilterEnabled)
        put("earphoneOptimized", preset.earphoneOptimized)
        put("description", preset.description)
      }
      jsonArray.put(obj)
    }
    prefs.edit().putString(KEY_CUSTOM_PRESETS, jsonArray.toString()).apply()
  }

  fun getSelectedPresetId(): String {
    return prefs.getString(KEY_SELECTED_PRESET_ID, BUILT_IN_PRESETS.first().id)
      ?: BUILT_IN_PRESETS.first().id
  }

  fun saveSelectedPresetId(id: String) {
    prefs.edit().putString(KEY_SELECTED_PRESET_ID, id).apply()
  }
}
