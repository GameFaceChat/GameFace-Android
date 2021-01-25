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
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener

/**
 * Recycler view click listener
 *
 * @property clickListener
 * @constructor
 *
 * @param context
 * @param recyclerView
 */
class RecyclerViewClickListener(context: Context?, recyclerView: RecyclerView, private val clickListener: ClickListener?) : OnItemTouchListener {
    private val gestureDetector: GestureDetector
    override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
        val child = rv.findChildViewUnder(e.x, e.y)
        if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
            clickListener.onClick(child, rv.getChildLayoutPosition(child))
        }
        return false
    }

    override fun onTouchEvent(rv: RecyclerView, e: MotionEvent) {}
    override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}

    /**
     * On click listener
     *
     * @constructor Create empty Click listener
     */
    interface ClickListener {
        /**
         * On click
         *
         * @param view
         * @param position
         */
        fun onClick(view: View?, position: Int)

        /**
         * On long click
         *
         * @param view
         * @param position
         */
        fun onLongClick(view: View?, position: Int)
    }

    init {
        gestureDetector = GestureDetector(context, object : SimpleOnGestureListener() {
            override fun onSingleTapUp(e: MotionEvent): Boolean {
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val child = recyclerView.findChildViewUnder(e.x, e.y)
                if (child != null && clickListener != null) {
                    clickListener.onLongClick(child, recyclerView.getChildAdapterPosition(child))
                }
            }
        })
    }
}