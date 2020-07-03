package com.takusemba.cropme.internal

internal interface ScaleAnimator {
  fun scale(scale: Float)
  fun adjust()

  companion object {
    const val ORIGINAL_SCALE = 1f
    const val ADJUSTING_DURATION = 600L
    const val ADJUSTING_FACTOR = 2f
  }
}

