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

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Circle Overlay.
 *
 * This overlay creates a circle frame and draw borders if necessary.
 */
class CircleCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawCrop(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val x = measuredWidth / 2f
    val y = measuredHeight / 2f

    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()
    val radius = maxOf(frameWidth, frameHeight) / 2f

    canvas.drawCircle(x, y, radius, paint)
  }

  override fun drawBorder(canvas: Canvas, paint: Paint) {
    val frameRect = frame ?: return
    val frameWidth = frameRect.width()
    val frameHeight = frameRect.height()

    val left = (width - frameWidth) / 2f
    val top = (height - frameHeight) / 2f
    val right = (width + frameWidth) / 2f
    val bottom = (height + frameHeight) / 2f

    val cx = measuredWidth / 2f
    val cy = measuredHeight / 2f
    val radius = maxOf(frameWidth, frameHeight) / 2f
    canvas.drawCircle(cx, cy, radius, paint)

    // x^2 + y^2 = z^2
    val x = radius / 3
    val z = radius
    val y = sqrt(z.pow(2) - (x).pow(2))
    val d = radius - y

    val borderHeight = frameHeight / 3
    canvas.drawLine(left + d, top + borderHeight, right - d, top + borderHeight, paint)
    canvas.drawLine(left + d, top + borderHeight * 2, right - d, top + borderHeight * 2, paint)

    val borderWidth = frameWidth / 3
    canvas.drawLine(left + borderWidth, top + d, left + borderWidth, bottom - d, paint)
    canvas.drawLine(left + borderWidth * 2, top + d, left + borderWidth * 2, bottom - d, paint)
  }
}