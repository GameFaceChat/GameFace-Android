package com.takusemba.cropme.internal

import android.view.MotionEvent

/**
 * ActionListener to notify action events that is needed for moving/scaling a view.
 */
internal interface ActionListener {
  fun onScaled(scale: Float)

  fun onScaleEnded()

  fun onMoved(dx: Float, dy: Float)
  fun onFlinged(velocityX: Float, velocityY: Float)
  fun onMoveEnded()
}