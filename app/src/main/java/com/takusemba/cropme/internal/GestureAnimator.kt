/*
 * Copyright (c) 2021 -Srihari Vishnu - All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.takusemba.cropme.internal

import android.graphics.RectF
import android.util.Log
import android.view.MotionEvent
import android.view.View

/**
 * Animator to move a view horizontally and vertically, and scale a view.
 */
internal class GestureAnimator(
    private val horizontalAnimator: MoveAnimator,
    private val verticalAnimator: MoveAnimator,
    private val scaleAnimator: ScaleAnimator
) : ActionListener {

  override fun onScaled(scale: Float) {
    scaleAnimator.scale(scale)
  }

  override fun onScaleEnded() {
    scaleAnimator.adjust()
  }

  override fun onMoved(dx: Float, dy: Float) {
    horizontalAnimator.move(dx)
    verticalAnimator.move(dy)
  }

  override fun onFlinged(velocityX: Float, velocityY: Float) {
    horizontalAnimator.fling(velocityX)
    verticalAnimator.fling(velocityY)
  }

  override fun onMoveEnded() {
    horizontalAnimator.adjust()
    verticalAnimator.adjust()
  }

//  fun adjust() {
//    horizontalAnimator.adjust()
//    verticalAnimator.adjust()
//    scaleAnimator.adjust()
//  }

  companion object {

    fun of(target: View, frame: RectF, scale: Float): GestureAnimator {
      val horizontalAnimator = HorizontalAnimatorImpl(
          targetView = target,
          leftBound = frame.left,
          rightBound = frame.right,
          maxScale = scale
      )
      val verticalAnimator = VerticalAnimatorImpl(
          targetView = target,
          topBound = frame.top,
          bottomBound = frame.bottom,
          maxScale = scale
      )
      val scaleAnimator = ScaleAnimatorImpl(
          targetView = target,
          maxScale = scale
      )
      return GestureAnimator(horizontalAnimator, verticalAnimator, scaleAnimator)
    }
  }
}