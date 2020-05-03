package com.magnitudestudios.GameFace

import android.app.Application
import android.util.Log
import com.magnitudestudios.GameFace.Network.FirebaseHelper

class GameFace : Application() {
    override fun onCreate() {
        super.onCreate()
        //        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

    override fun onLowMemory() {
        super.onLowMemory()
        Log.e("APPLICATION", "onLowMemory")
    }
}