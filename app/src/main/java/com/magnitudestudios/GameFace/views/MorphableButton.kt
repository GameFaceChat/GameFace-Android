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
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatButton
import androidx.core.animation.addListener
import androidx.core.content.ContextCompat
import com.magnitudestudios.GameFace.R

class MorphableButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : AppCompatButton(context, attrs, defStyle) {

    private var state = State.IDLE
    private val drawable = ContextCompat.getDrawable(context, R.drawable.morphable_btn_background) as GradientDrawable
    private var animationInProgress = false

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
        val initialCornerRadius = resources.getDimension(R.dimen.rounded_rect).toFloat()

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
            layoutParams.width = it.animatedValue as Int
        }

        val mAnimationSet = AnimatorSet()
        mAnimationSet.duration = 300
        mAnimationSet.playTogether(cornerAnimation, widthAnimation)

        mAnimationSet.addListener { animationInProgress = false }

        mAnimationSet.start()

    }

    fun setLoading(boolean: Boolean) {
        if (boolean) {
            startAnimation()
        }
    }

}