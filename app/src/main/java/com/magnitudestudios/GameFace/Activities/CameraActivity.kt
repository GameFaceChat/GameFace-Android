/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.Activities

import android.os.Bundle
import com.magnitudestudios.GameFace.Bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.R

class CameraActivity : BasePermissionsActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)
    }
}

