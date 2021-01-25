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

package com.magnitudestudios.GameFace.views

import android.animation.PropertyValuesHolder
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ProgressBar
import androidx.cardview.widget.CardView
import androidx.core.animation.doOnEnd
import org.webrtc.EglBase
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * Movable screen
 *
 * @constructor
 *
 * @param context
 * @param attributeSet
 * @param styleAttr
 * @param overlay
 */
class MovableScreen @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null,
        styleAttr: Int = 0,
        overlay: Boolean = true) : CardView(context, attributeSet, styleAttr) {

    val surface: CustomSurfaceView = CustomSurfaceView(context).apply {
        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        setZOrderMediaOverlay(overlay)
    }

    private val progressBar: ProgressBar = ProgressBar(context).apply {
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT).apply {
            gravity = Gravity.CENTER
            visibility = View.GONE
        }
    }

    private var dX = 0f
    private var dY = 0f
    var maxwidth = 0f
    var maxheight = 0f

    private var animInProgress = false

    private var stateConnected = false
    private var stateFull = true

    private val parentDimens: Unit
        get() {
            maxwidth = (parent as View).width.toFloat()
            maxheight = (parent as View).height.toFloat()
        }

    /**
     * Initialize
     *
     * @param eglBase
     * @param overlay
     * @param onTop
     */
    fun initialize(eglBase: EglBase, overlay: Boolean, onTop : Boolean = false) {
        surface.apply {
            setZOrderMediaOverlay(overlay)
            setZOrderOnTop(onTop)
            init(eglBase.eglBaseContext, null)
        }

        addView(surface)
        addView(progressBar)
        radius = 50f


    }

    /**
     * Sets the Screen into disconnected mode
     *
     */
    fun setDisconnected() {
        this.surface.clearImage()
    }

    /**
     * Set loading
     *
     * @param isLoading
     *///Shows the progress bar
    fun setLoading(isLoading: Boolean) {
        progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event == null || stateFull || animInProgress) return false
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                dX = this.x - event.rawX
                dY = this.y - event.rawY
            }
            MotionEvent.ACTION_MOVE -> {
                animate()
                        .translationX(max(0f, min(event.rawX + dX, maxwidth - width)))
                        .translationY(max(0f, min(event.rawY + dY, maxheight - height)))
                        .setDuration(0)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .start()
            }
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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        parentDimens
    }

    private fun closeEnough(n1: Float, n2: Float): Boolean {
        return abs(n1 - n2) < 10
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

    private fun scaleAnimator(startX: Float, endX: Float, startY: Float, endY: Float) {
        val pvhX = PropertyValuesHolder.ofInt("x", startX.roundToInt(), endX.roundToInt())
        val pvhY = PropertyValuesHolder.ofInt("y", startY.roundToInt(), endY.roundToInt())
        val animator = ValueAnimator.ofPropertyValuesHolder(pvhX, pvhY)
        animator.addUpdateListener { animation: ValueAnimator ->
            val layoutParams = layoutParams
            layoutParams.width = animation.getAnimatedValue("x") as Int
            layoutParams.height = animation.getAnimatedValue("y") as Int
            setLayoutParams(layoutParams)
            invalidate()
        }
        animator.doOnEnd {
            animInProgress = false
        }
        animator.duration = 300
        animInProgress = true
        animator.start()
    }

    /**
     * Set calling
     *
     */// Set this surface view as a smaller screen
    fun setCalling() {
        setSmallScreen()
        stateConnected = true
    }

    /**
     * Sets the screen into local
     *
     */// Sets this as a fullscreen view
    fun setLocal() {
        setFullScreen()
        stateConnected = false
    }

    //Sets as a small screen
    private fun setSmallScreen() {
        setDimensionsSmallScreen()
        stateFull = false
    }

    //Sets as full screen
    private fun setFullScreen() {
        reposition(null, 0f, 0f)
        setDimensionsFullScreen()
        stateFull = true
    }

    //Sets the new dimensions of the view (for small screen)
    private fun setDimensionsSmallScreen() {
        scaleAnimator(width.toFloat(), maxwidth / 4, height.toFloat(), maxheight / 4)
    }

    //Sets the new dimensions of the view (for full screen)
    private fun setDimensionsFullScreen() {
        scaleAnimator(width.toFloat(), maxwidth, height.toFloat(), maxheight - 100)
    }
}