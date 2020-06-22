/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.calling

import android.os.Bundle
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity

class IncomingCall : BasePermissionsActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_incoming_call)
    }
}