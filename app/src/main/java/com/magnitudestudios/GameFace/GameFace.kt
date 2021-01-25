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

package com.magnitudestudios.GameFace

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

/**
 * This is the Base Application class
 *
 * @constructor
 */
class GameFace : Application() {
    override fun onCreate() {
        super.onCreate()
        Firebase.database.setPersistenceEnabled(true)
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val friendsChannel = NotificationChannel(
                    getString(R.string.friends_notification_ID),
                    getString(R.string.friends_notification_channel),
                    NotificationManager.IMPORTANCE_DEFAULT).apply {
                description = getString(R.string.friends_notification_description)
            }

            val callsChannel = NotificationChannel(
                    getString(R.string.calling_notification_ID),
                    getString(R.string.calling_notification_channel),
                    NotificationManager.IMPORTANCE_HIGH).apply {
                        description = getString(R.string.calling_notification_description)
                        vibrationPattern = Constants.VIBRATE_PATTERN
                        lockscreenVisibility = NotificationCompat.VISIBILITY_PUBLIC
                    }

            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannels(listOf(friendsChannel, callsChannel))
        }
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.e("APPLICATION", "onLowMemory")
    }
}