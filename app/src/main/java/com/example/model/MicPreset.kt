package com.example.model

data class MicPreset(
  val id: String,
  val name: String,
  val isBuiltIn: Boolean = false,
  val boostLevel: Float = 2.0f, // 1.0f (0dB) to 4.0f (+18dB)
  val agcEnabled: Boolean = true,
  val noiseSuppressionEnabled: Boolean = true,
  val echoCancelerEnabled: Boolean = true,
  val clarityBoostEnabled: Boolean = true,
  val lowCutFilterEnabled: Boolean = true,
  val earphoneOptimized: Boolean = true,
  val description: String = ""
) {
  val boostPercentage: Int
    get() = (boostLevel * 100).toInt()

  val boostDbApprox: String
    get() {
      val db = 20 * kotlin.math.log10(boostLevel)
      return if (db <= 0.1f) "+0.0 dB" else "+%.1f dB".format(db)
    }
}
