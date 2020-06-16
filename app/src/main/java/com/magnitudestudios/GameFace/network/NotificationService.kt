/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.network


import android.app.Activity
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Intent
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.ui.login.LoginActivity
import com.magnitudestudios.GameFace.ui.main.MainActivity
import kotlinx.coroutines.*
import java.lang.Exception
import java.util.*

class NotificationService : FirebaseMessagingService() {
    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.Main + serviceJob)
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        try {

            serviceScope.launch {
                if (Firebase.auth.currentUser != null) {
                    FirebaseHelper.updateDeviceToken(token)
                }
            }
        } catch (e: Exception) {
            Log.e("ERROR", "ON NEW DEVICE TOKEN", e)
        }

        Log.e("--NEW TOKEN--", token)
    }

    override fun onMessageReceived(p0: RemoteMessage) {
        super.onMessageReceived(p0)
        for (a in p0.data) {
            Log.e("Got Data", "${a.key} : ${a.value}")
        }
        val intent = Intent(this, MainActivity::class.java)
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        val builder = NotificationCompat.Builder(this, "CHANNEL123")
                .setSmallIcon(R.drawable.ic_add_friend)
                .setContentTitle("My notification")
                .setContentText("Hello World!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .addAction(R.drawable.ic_add_friend, "Add Friend",
                        resultPendingIntent)
        // Called during activity
        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(Random().nextInt(10000000), builder.build())
        }
//        Toast.makeText(applicationContext, "Got a notification", Toast.LENGTH_LONG).show()
        Log.e("Received Message", "NICE")
    }

    override fun onMessageSent(p0: String) {
        super.onMessageSent(p0)
    }
}