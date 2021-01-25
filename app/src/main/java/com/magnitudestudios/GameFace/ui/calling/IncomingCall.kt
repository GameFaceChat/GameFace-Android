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

package com.magnitudestudios.GameFace.ui.calling

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityIncomingCallBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.repository.SessionRepository
import com.magnitudestudios.GameFace.ui.main.MainActivity

/**
 * Incoming call
 *
 * @constructor Create empty Incoming call
 */
class IncomingCall : BasePermissionsActivity() {
    private lateinit var bind: ActivityIncomingCallBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //Delete the notification
        deleteNotification()

        bind = ActivityIncomingCallBinding.inflate(layoutInflater)
        setContentView(bind.root)

        if (!intent.hasExtra(Constants.ROOM_ID_KEY) || !intent.hasExtra(Constants.ROOM_MEMBERS_KEY)) finish()
        else if (Firebase.auth.currentUser == null) finish()

        val roomID = intent.getStringExtra(Constants.ROOM_ID_KEY)!!

        //Load the member profiles
        val memberProfiles = try {
            Gson().fromJson(intent.getStringExtra(Constants.ROOM_MEMBERS_KEY), object : TypeToken<List<Profile>>() {}.type) as List<Profile>
        } catch (e: Exception) {
            Log.e("INCOMING CALL", "Error upon deserialize JSON: "+ intent.getStringExtra(Constants.ROOM_MEMBERS_KEY), e)
            finish()
            ArrayList<Profile>()
        }

        //Update the UI of the Incoming call screen
        Glide.with(this)
                .load(memberProfiles[0].profilePic)
                .placeholder(R.drawable.profile_placeholder)
                .error(R.drawable.ic_user_placeholder)
                .circleCrop()
                .into(bind.profilePic)

        bind.usernames.text = memberProfiles.joinToString(",") { it.username }

        bind.denyCall.setOnClickListener {
            SessionRepository.denyCall(Firebase.auth.currentUser!!.uid, roomID)
            finish()
        }

        bind.acceptCall.setOnClickListener {
            SessionRepository.acceptCall(Firebase.auth.currentUser!!.uid, roomID)
            val toMainActivity = Intent(this, MainActivity::class.java)
            toMainActivity.putExtra(Constants.ROOM_ID_KEY, roomID)
            toMainActivity.putExtra(Constants.CALL_KEY, "true")
            startActivity(toMainActivity)
            finish()
        }
    }

    private fun deleteNotification() {
        with (NotificationManagerCompat.from(this)) { cancel(Constants.INCOMING_CALL_ID) }
    }
}