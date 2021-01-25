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
import android.graphics.ColorFilter
import android.util.AttributeSet
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout


/**
 * Custom input layout
 *
 * @constructor
 *
 * @param context
 * @param attrs
 * @param defStyleAttr
 */
class CustomInputLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : TextInputLayout(context, attrs, defStyleAttr) {
    override fun setError(@Nullable error: CharSequence?) {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.setError(error)
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    override fun drawableStateChanged() {
        val defaultColorFilter = getBackgroundDefaultColorFilter()
        super.drawableStateChanged()
        //Reset EditText's background color to default.
        updateBackgroundColorFilter(defaultColorFilter)
    }

    private fun updateBackgroundColorFilter(colorFilter: ColorFilter?) {
        if (editText != null && editText?.background != null) editText!!.background.colorFilter = colorFilter
    }

    @Nullable
    private fun getBackgroundDefaultColorFilter(): ColorFilter? {
        var defaultColorFilter: ColorFilter? = null
        if (editText != null && editText?.background != null) defaultColorFilter = DrawableCompat.getColorFilter(editText!!.background)
        return defaultColorFilter
    }
}