/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import android.content.Context
import android.graphics.ColorFilter
import android.util.AttributeSet
import androidx.annotation.Nullable
import androidx.core.graphics.drawable.DrawableCompat
import com.google.android.material.textfield.TextInputLayout


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