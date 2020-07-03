/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.takusemba.cropme.internal

import android.view.MotionEvent

interface TouchListener {
    fun onTouch(event: MotionEvent?)
}