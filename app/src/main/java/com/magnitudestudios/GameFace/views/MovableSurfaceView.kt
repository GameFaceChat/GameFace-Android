package com.magnitudestudios.GameFace.views

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import org.webrtc.SurfaceViewRenderer

class MovableSurfaceView : SurfaceViewRenderer {

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        super.surfaceChanged(holder, format, width, height)
        Log.e("LOCAL", "surfaceChanged: $width $height")
    }
}