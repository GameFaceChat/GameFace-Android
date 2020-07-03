package com.takusemba.cropme.internal

import androidx.dynamicanimation.animation.SpringForce

internal interface MoveAnimator {
  fun move(delta: Float)
  fun adjust()
  fun fling(velocity: Float)

  companion object {
    const val STIFFNESS = SpringForce.STIFFNESS_VERY_LOW
    const val DAMPING_RATIO = SpringForce.DAMPING_RATIO_NO_BOUNCY
    const val FRICTION = 3f
  }
}
