package com.magnitudestudios.GameFace.views

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.util.Log
import android.view.MotionEvent
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import org.webrtc.SurfaceViewRenderer

class MovableSurfaceView : SurfaceViewRenderer {
    var dX = 0f
    var dY = 0f
    var maxwidth = 0f
    var maxheight = 0f
    var stateConnected = false
    var stateFull = true

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {}

    private val parentDimens: Unit
        private get() {
            maxwidth = (parent as View).width.toFloat()
            maxheight = (parent as View).height.toFloat()
            Log.e("INIT", "initView: $maxwidth $maxheight")
        }

    fun setCalling() {
        setSmallScreen()
        stateConnected = true
    }

    fun setLocal() {
        setFullScreen()
        stateConnected = false
    }

    private fun setSmallScreen() {
        setDimensionsSmallScreen()
        stateFull = false
    }

    private fun setFullScreen() {
        reposition(null, 0f, 0f)
        setDimensionsFullScreen()
        stateFull = true
    }

    private fun setDimensionsSmallScreen() {
        parentDimens
        scaleAnimator(width.toFloat(), maxwidth / 3, height.toFloat(), maxheight / 3)
    }

    private fun setDimensionsFullScreen() {
        parentDimens
        scaleAnimator(width.toFloat(), maxwidth, height.toFloat(), maxheight)
    }

    private fun scaleAnimator(startX: Float, endX: Float, startY: Float, endY: Float) {
        val pvhX = PropertyValuesHolder.ofInt("x", Math.round(startX), Math.round(endX))
        val pvhY = PropertyValuesHolder.ofInt("y", Math.round(startY), Math.round(endY))
        val animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        animator.addUpdateListener { animation: ValueAnimator ->
            val layoutParams = layoutParams
            layoutParams.width = animation.getAnimatedValue("x") as Int
            layoutParams.height = animation.getAnimatedValue("y") as Int
            setLayoutParams(layoutParams)
        }
        animator.duration = 300
        animator.start()
    }

    override fun surfaceChanged(holder: SurfaceHolder, format: Int, width: Int, height: Int) {
        super.surfaceChanged(holder, format, width, height)
        Log.e("LOCAL", "surfaceChanged: $width $height")
    }

    override fun setOnClickListener(l: OnClickListener?) {
        super.setOnClickListener(l)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                parentDimens
                dX = this.x - event.rawX
                dY = this.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> animate()
                    .translationX(Math.max(0f, Math.min(event.rawX + dX, maxwidth - width)))
                    .translationY(Math.max(0f, Math.min(event.rawY + dY, maxheight - height)))
                    .setDuration(0)
                    .setInterpolator(AccelerateDecelerateInterpolator())
                    .start()
            MotionEvent.ACTION_UP -> {
                Log.e("Event", "onTouchEvent: " + (event.eventTime - event.downTime))
                if (event.eventTime - event.downTime < 100 && stateConnected
                        && closeEnough(this.x - event.rawX, dX) && closeEnough(this.y - event.rawY, dY)) {
                    if (stateFull) {
                        setSmallScreen()
                    } else {
                        setFullScreen()
                    }
                } else {
                    reposition(event)
                }
            }
            else -> return false
        }
        return true
    }

    private fun closeEnough(n1: Float, n2: Float): Boolean {
        return Math.abs(n1 - n2) < 10
    }

    private fun reposition(event: MotionEvent?, vararg args: Float) {
        var positionx = 0.0f
        var positiony = 0.0f
        if (event != null) {
            positionx = event.rawX + dX
            positiony = event.rawY + dY
        }
        val toX: Float
        val toY: Float
        val horizontal_boundary = maxwidth / 2 - width / 2.0f
        val vertical_boundary = maxheight / 2 - height / 2.0f

        //Top-Left
        if (args.size == 2 && event == null) {
            toX = args[0]
            toY = args[1]
        } else if (positionx <= horizontal_boundary && positiony <= vertical_boundary) {
            toX = 0f
            toY = 0f
        } else if (positionx <= horizontal_boundary && positiony >= vertical_boundary) {
            toX = 0f
            toY = maxheight - height
        } else if (positionx >= horizontal_boundary && positiony <= vertical_boundary) {
            toX = maxwidth - width
            toY = 0f
        } else {
            toX = maxwidth - width
            toY = maxheight - height
        }
        animate()
                .translationX(toX)
                .translationY(toY)
                .setDuration(400)
                .setInterpolator(AccelerateDecelerateInterpolator())
                .start()
    }

    companion object {
        private fun convertPixelsToDp(px: Float, context: Context): Float {
            return px / (context.resources.displayMetrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)
        }
    }
}