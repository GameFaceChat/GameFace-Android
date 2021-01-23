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

import android.annotation.SuppressLint
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import androidx.core.view.GestureDetectorCompat

/**
 * According to the gesture detected on [trackPad], this will notify [actionListener].
 */
internal class GestureAnimation(
    private val trackPad: View,
    private val touchListener: TouchListener,
    private val actionListener: ActionListener
) {

  private val gestureListener = object : GestureDetector.SimpleOnGestureListener() {
    override fun onDown(e: MotionEvent?): Boolean {
      return true
    }
    override fun onShowPress(e: MotionEvent) = Unit

    override fun onSingleTapUp(e: MotionEvent): Boolean {
      return true
    }

    override fun onScroll(
        initialEvent: MotionEvent, currentEvent: MotionEvent, dx: Float, dy: Float
    ): Boolean {
      actionListener.onMoved(-dx, -dy)
      return true
    }

    override fun onLongPress(e: MotionEvent) = Unit

    override fun onFling(
        e1: MotionEvent, e2: MotionEvent, velocityX: Float, velocityY: Float
    ): Boolean {
      actionListener.onFlinged(velocityX, velocityY)
      return true
    }
  }

  private val scaleListener = object : ScaleGestureDetector.SimpleOnScaleGestureListener() {

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
      return super.onScaleBegin(detector)
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
      super.onScaleEnd(detector)
      actionListener.onScaleEnded()
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
      actionListener.onScaled(detector.scaleFactor)
      return true
    }
  }

  private val gestureDetector = GestureDetectorCompat(trackPad.context, gestureListener)
  private val scaleDetector = ScaleGestureDetector(trackPad.context, scaleListener)

  @SuppressLint("ClickableViewAccessibility")
  fun start() {

    trackPad.setOnTouchListener { _, event ->
      gestureDetector.onTouchEvent(event)
      scaleDetector.onTouchEvent(event)
      touchListener.onTouch(event)
      when (event.action) {
        MotionEvent.ACTION_UP -> actionListener.onMoveEnded()
      }
      true
    }
  }

  fun stop() {
    trackPad.setOnTouchListener(null)
  }
}