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

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.recyclerview.widget.ItemTouchHelper
import kotlin.math.max

/**
 * Video layout
 *
 * @constructor
 *
 * @param context
 * @param attrs
 * @param defStyle
 */
class VideoLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0) : ViewGroup(context, attrs, defStyle) {

    val validViews = mutableListOf<View>()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        Log.e("DIMENSIONS MEASURE", "$widthMeasureSpec, $heightMeasureSpec")

        var childState = 0
        validViews.clear()
        forEach { child -> if (child.visibility != View.GONE) validViews.add(child) }

        for (i in 0 until validViews.size) {
            val child = getChildAt(i)
            var width = 0
            var height = 0
            val isLast = i == validViews.size - 1
            childState = View.combineMeasuredStates(childState, child.measuredState)
            when (validViews.size) {
                1,2,3 -> {
                    width = max(0, measuredWidth)
                    height = max(0, measuredHeight / validViews.size)
                }
                4 -> {
                    width = max(0, measuredWidth / 2)
                    height = max(0, measuredHeight / 2)
                }
                5 -> {
                    width = if (isLast) max(0, measuredWidth) else max(0, measuredWidth / 2)
                    height = max(0, measuredHeight / 3)
                }
                6 -> {
                    width = max(0, measuredWidth / 2)
                    height = max(0, measuredHeight / 3)
                }
            }


            val childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(width, MeasureSpec.EXACTLY)
            val childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec)
        }

        setMeasuredDimension(View.resolveSizeAndState(suggestedMinimumWidth, widthMeasureSpec, childState),
                View.resolveSizeAndState(suggestedMinimumHeight, heightMeasureSpec,
                        childState shl View.MEASURED_HEIGHT_STATE_SHIFT))


    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        Log.e("DIMENSIONS LAYOUT", "$l, $t, $r, $b")
        for (i in 0 until validViews.size) {
            val child = getChildAt(i)

            var childLeft = 0
            var childRight = 0
            var childTop = 0
            var childBottom = 0

            val isLast = i == validViews.size - 1

            when (validViews.size) {
                1,2,3 -> {
                    childLeft = l
                    childRight = childLeft + child.measuredWidth
                    childTop = t + measuredHeight * i / validViews.size
                    childBottom = childTop + child.measuredHeight
                }
                4 -> {
                    childLeft = l + (i % 2) * measuredWidth / 2
                    childRight = childLeft + child.measuredWidth
                    childTop = t + (if (i >= 2) 1 else 0) * measuredHeight / 2
                    childBottom = childTop + child.measuredHeight
                }
                5 -> {
                    childLeft = l + (i % 2) * measuredWidth / 2
                    childRight = childLeft + child.measuredWidth
                    childTop = t + (if (isLast) 2 else if (i >= 2) 1 else 0) * measuredHeight / 3
                    childBottom = childTop + child.measuredHeight
                }
                6 -> {
                    childLeft = l + (i % 2) * measuredWidth / 2
                    childRight = childLeft + child.measuredWidth
                    childTop = t + (i % 3) * measuredHeight / 3
                    childBottom = childTop + child.measuredHeight
                }
            }

            child.layout(childLeft, childTop, childRight, childBottom)
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {

        return super.onTouchEvent(event)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun shouldDelayChildPressedState(): Boolean {
        return false
    }

    override fun generateDefaultLayoutParams(): ViewGroup.LayoutParams {
        return LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
    }

    /**
     * Layout params
     *
     * @constructor Create empty Layout params
     */
    open class LayoutParams : ViewGroup.LayoutParams {

        constructor(width: Int, height: Int) : super(width, height) {}


        constructor(source: ViewGroup.LayoutParams) : super(source) {}
        constructor(source: MarginLayoutParams) : super(source) {}

    }

}