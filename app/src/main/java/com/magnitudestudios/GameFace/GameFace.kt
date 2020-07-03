package com.magnitudestudios.GameFace

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat

class GameFace : Application() {
    override fun onCreate() {
        super.onCreate()
        //        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
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