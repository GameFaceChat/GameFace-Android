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

package com.takusemba.cropme.overlays

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import com.magnitudestudios.GameFace.R


/**
 * Overlay to show a frame with some decorations like borders.
 *
 * You have to extend [CropOverlay] to create your own custom overlay.
 * You have to override [drawCrop], and optionally [drawBackground], [drawBorder]
 */
abstract class CropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : FrameLayout(context, attrs, defStyleAttr) {

  private val backgroundPaint: Paint
  private val cropPaint: Paint
  private val borderPaint: Paint?
  private val backgroundAlpha: Float

  private val fadeAnim: ValueAnimator

  protected var frame: RectF? = null
    private set

  init {
    val withBorder: Boolean

    if (cropOverlayAttrs != null) {
      val a = context.obtainStyledAttributes(cropOverlayAttrs, R.styleable.CropOverlay, 0, 0)
      try {
        backgroundAlpha = a.getFraction(
            R.styleable.CropLayout_cropme_background_alpha,
                DEFAULT_BASE,
                DEFAULT_PBASE,
                DEFAULT_BACKGROUND_ALPHA
        )
        withBorder = a.getBoolean(
            R.styleable.CropOverlay_cropme_with_border,
                DEFAULT_WITH_BORDER
        )
      } finally {
        a.recycle()
      }
    } else {
      backgroundAlpha = DEFAULT_BACKGROUND_ALPHA
      withBorder = DEFAULT_WITH_BORDER
    }
    setWillNotDraw(false)
    setLayerType(View.LAYER_TYPE_HARDWARE, null)

    backgroundPaint = Paint().apply {
      color = ContextCompat.getColor(context, android.R.color.black)
      alpha = (backgroundAlpha * COLOR_DENSITY).toInt()
    }

    cropPaint = Paint().apply {
      xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR)
    }

    borderPaint = if (withBorder) Paint().apply {
      strokeWidth = BORDER_WIDTH.toFloat()
      style = Paint.Style.STROKE
      color = ContextCompat.getColor(context, R.color.white)
      alpha = 0
    } else null

    fadeAnim = ValueAnimator.ofInt((backgroundAlpha * COLOR_DENSITY).toInt(), 0)
    val mDuration = FADE_ANIMATION_TIME//in millis

    fadeAnim.duration = mDuration.toLong()
    fadeAnim.interpolator = DecelerateInterpolator()
    fadeAnim.addUpdateListener {
      borderPaint?.alpha = it.animatedValue as Int
      invalidate()
    }
  }

  override fun onDraw(canvas: Canvas) {
    super.onDraw(canvas)
    drawBackground(canvas, backgroundPaint)
    drawCrop(canvas, cropPaint)
    if (borderPaint != null) {
      drawBorder(canvas, borderPaint)
    }
  }

  open fun drawBackground(canvas: Canvas, paint: Paint) {
    canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), backgroundPaint)
  }

  abstract fun drawCrop(canvas: Canvas, paint: Paint)

  abstract fun drawBorder(canvas: Canvas, paint: Paint)

  fun setFrame(frame: RectF) {
    this.frame = frame
  }

  final override fun setWillNotDraw(willNotDraw: Boolean) {
    super.setWillNotDraw(willNotDraw)
  }

  final override fun setLayerType(layerType: Int, paint: Paint?) {
    super.setLayerType(layerType, paint)
  }

  override fun onTouchEvent(event: MotionEvent?): Boolean {
    if (event == null) return true
    if (event.action == MotionEvent.ACTION_DOWN) {
      fadeAnim.cancel()
      borderPaint?.alpha = (backgroundAlpha * COLOR_DENSITY).toInt()
      invalidate()
    }
    else if (event.action == MotionEvent.ACTION_UP) {
      animateFade()
    }
    return true
  }

  private fun animateFade() {
    fadeAnim.start()
  }

  private fun startBorderAnimation() {

  }
  companion object {

    private const val BORDER_WIDTH = 4

    private const val DEFAULT_BASE = 1
    private const val DEFAULT_PBASE = 1

    private const val DEFAULT_BACKGROUND_ALPHA = 0.8f
    private const val COLOR_DENSITY = 255f

    private const val DEFAULT_WITH_BORDER = true
    private const val FADE_ANIMATION_TIME = 2000
  }
}