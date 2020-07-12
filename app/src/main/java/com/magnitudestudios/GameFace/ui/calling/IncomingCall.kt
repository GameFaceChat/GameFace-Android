/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.calling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityIncomingCallBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.ui.main.MainActivity

class IncomingCall : BasePermissionsActivity() {
    private lateinit var bind: ActivityIncomingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bind = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (!intent.hasExtra(Member::roomID.name) || !intent.hasExtra(Constants.ROOM_MEMBERS_KEY)) finish()

        val memberProfiles = try {
            Gson().fromJson(intent.getStringExtra(Constants.ROOM_MEMBERS_KEY), object : TypeToken<List<Profile>>() {}.type) as List<Profile>
        } catch (e: Exception) {
            Log.e("INCOMING CALL", "Error when deserializing JSON", e)
            finish()
            null
        }

        Glide.with(this)
                .load(memberProfiles?.get(0)?.profilePic)
                .error(R.drawable.ic_add_profile_pic)
                .circleCrop()
                .into(bind.profilePic)


        bind.denyCall.setOnClickListener { finish() }

        bind.acceptCall.setOnClickListener {
            val toMainActivity = Intent(this, MainActivity::class.java)
            toMainActivity.putExtra(Constants.ROOM_ID_KEY, intent.getStringExtra(Member::roomID.name))
            toMainActivity.putExtra(Constants.CALL_KEY, "true")
            startActivity(toMainActivity)
            finish()
        }
    }
}