/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */
package com.magnitudestudios.GameFace.views

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.graphics.drawable.Animatable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.DecelerateInterpolator
import android.view.animation.LinearInterpolator
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.addListener
import androidx.core.animation.doOnEnd
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.updateBounds
import androidx.core.view.updateLayoutParams
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.magnitudestudios.GameFace.R

class MorphableButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : AppCompatButton(context, attrs, defStyle), LifecycleObserver {

    private var state = State.IDLE
    private val drawable = ContextCompat.getDrawable(context, R.drawable.morphable_btn_background) as GradientDrawable
    private var animationInProgress = false
    private var mAnimatedDrawable: CircularAnimatedDrawable? = null
    private enum class State {
        PROGRESS, IDLE
    }

    init {
        background = drawable
        text = "SRIHARI"
    }

    fun startAnimation() {
        if (state == State.PROGRESS) return
        val initialWidth = width
        val initialHeight = height
        val initialCornerRadius = resources.getDimension(R.dimen.rounded_rect)

        state = State.PROGRESS
        animationInProgress = true

        text = null
        isClickable = false


        val toWidth = initialHeight
        val cornerAnimation = ObjectAnimator.ofFloat(
                drawable,
                "cornerRadius",
                initialCornerRadius,
                1000.0f)
        val widthAnimation = ValueAnimator.ofInt(initialWidth, toWidth);
        widthAnimation.addUpdateListener {
            updateLayoutParams { width = it.animatedValue as Int }
        }

        val mAnimationSet = AnimatorSet().apply {
            duration = 300
            playTogether(cornerAnimation, widthAnimation)
        }
        mAnimationSet.doOnEnd { animationInProgress = false}
        mAnimationSet.start()
    }

    private fun drawIntermediateProgress(canvas: Canvas) {
        if (mAnimatedDrawable == null || mAnimatedDrawable!!.isRunning) {
            mAnimatedDrawable = CircularAnimatedDrawable(this, 10f, Color.WHITE)
            val offset = (width - height)/2
            mAnimatedDrawable!!.updateBounds(offset, 0, width - offset, height)
            mAnimatedDrawable!!.callback = this
            mAnimatedDrawable!!.start()
        } else {
            mAnimatedDrawable!!.draw(canvas)
        }
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
//        if (state == State.PROGRESS && !animationInProgress) {
//            canvas!!.drawArc(RectF(0f, 0f,100f,100f), 0f, 360f, true, paint)
        if (state == State.PROGRESS && !animationInProgress) drawIntermediateProgress(canvas!!)
//        }
    }

    fun setLoading(boolean: Boolean) {
        if (boolean) {
            startAnimation()
        }
    }
    @OnLifecycleEvent(androidx.lifecycle.Lifecycle.Event.ON_DESTROY)
    fun dispose() {
        mAnimatedDrawable?.stop()
    }

    class CircularAnimatedDrawable(val animView: View, val borderWidth: Float, arcColor: Int) : Drawable(), Animatable {
        private val paint: Paint = Paint()
        private var inProgress = false
        private var valueAnimatorAngle: ValueAnimator? = null
        private var valueAnimatorSweep: ValueAnimator? = null

        private var mCurrentGlobalAngle = 0f
        private var mCurrentSweepAngle = 0f
        private var mCurrentGlobalAngleOffset = 0f

        private val mBounds: RectF by lazy {
            RectF().apply {
                left = bounds.left.toFloat() + borderWidth / 2F + .5F
                right = bounds.right.toFloat() - borderWidth / 2F - .5F
                top = bounds.top.toFloat() + borderWidth / 2F + .5F
                bottom = bounds.bottom.toFloat() - borderWidth / 2F - .5F
            }
        }
        private var modeAppearing = false

        init {
            paint.apply {
                isAntiAlias = true
                style = Paint.Style.STROKE
                strokeWidth = borderWidth
                color = arcColor
            }
            setUpAnimations()
        }

        override fun draw(canvas: Canvas) {
            var startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
            var sweepAngle = mCurrentSweepAngle
            if (modeAppearing) {
                startAngle = mCurrentGlobalAngle - mCurrentGlobalAngleOffset
                sweepAngle = mCurrentSweepAngle + 30f
            } else {
                startAngle = (mCurrentGlobalAngle - mCurrentGlobalAngleOffset + mCurrentSweepAngle)
                sweepAngle = 360F - mCurrentSweepAngle - 30f
            }
            canvas.drawArc(mBounds, startAngle, sweepAngle, false, paint)
        }

        override fun setAlpha(alpha: Int) {
            paint.alpha = alpha
        }

        override fun getOpacity(): Int { return PixelFormat.TRANSPARENT }

        override fun setColorFilter(colorFilter: ColorFilter?) {
            paint.colorFilter = colorFilter
        }

        override fun isRunning(): Boolean { return false }

        override fun start() {
            if (inProgress) return
            inProgress = true
            valueAnimatorAngle?.start()
            valueAnimatorSweep?.start()

        }

        override fun stop() {
            if (!inProgress) return
            inProgress = false
            valueAnimatorAngle?.cancel()
            valueAnimatorSweep?.cancel()
        }

        private fun toggleSweep() {
            modeAppearing = !modeAppearing

            if (modeAppearing) {
                mCurrentGlobalAngleOffset = (mCurrentGlobalAngleOffset + 30f * 2) % 360
            }
        }

        private fun setUpAnimations() {
            valueAnimatorAngle = ValueAnimator.ofFloat(0f, 360f).apply {
                interpolator = LinearInterpolator()
                duration = 2000
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    animView.invalidate()
                    mCurrentGlobalAngle = it.animatedValue as Float
                }
            }

            valueAnimatorSweep = ValueAnimator.ofFloat(0f, 360f - 2 * 30f).apply {
                interpolator = DecelerateInterpolator()
                duration = 900
                repeatCount = ValueAnimator.INFINITE
                addUpdateListener {
                    mCurrentSweepAngle = it.animatedValue as Float
                    invalidateSelf()
                }
                addListener {
                    //toggleAppearingMode()
                    toggleSweep()
                }
            }
        }
    }

}