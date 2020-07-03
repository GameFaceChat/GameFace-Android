package com.takusemba.cropme.overlays

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.util.AttributeSet
import com.takusemba.cropme.overlays.CropOverlay

/**
 * None Overlay.
 *
 * This overlay does nothing.
 */
class NoneCropOverlay @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    cropOverlayAttrs: AttributeSet? = attrs
) : CropOverlay(context, attrs, defStyleAttr, cropOverlayAttrs) {

  override fun drawBackground(canvas: Canvas, paint: Paint) = Unit

  override fun drawCrop(canvas: Canvas, paint: Paint) = Unit

  override fun drawBorder(canvas: Canvas, paint: Paint) = Unit
}