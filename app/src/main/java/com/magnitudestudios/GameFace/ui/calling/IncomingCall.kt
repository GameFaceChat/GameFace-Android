/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.calling

import android.content.Intent
import android.os.Bundle
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityIncomingCallBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.SendCall
import com.magnitudestudios.GameFace.ui.main.MainActivity

class IncomingCall : BasePermissionsActivity() {
    private lateinit var bind: ActivityIncomingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(bind.root)

        bind.denyCall.setOnClickListener { finish() }

        bind.username.text = intent.getStringExtra(Profile::username.name)
        bind.fullName.text = intent.getStringExtra(Profile::name.name)

        bind.acceptCall.setOnClickListener {
            val toMainActivity = Intent(this, MainActivity::class.java)
            toMainActivity.putExtra(Constants.ROOM_ID_KEY, intent.getStringExtra(SendCall::roomID.name))
            toMainActivity.putExtra(Constants.CALL_KEY, "true")
            startActivity(toMainActivity)
            finish()
        }
    }
}