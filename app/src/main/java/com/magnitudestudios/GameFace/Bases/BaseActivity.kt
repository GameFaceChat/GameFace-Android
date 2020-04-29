package com.magnitudestudios.GameFace.Bases

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.magnitudestudios.GameFace.GameFace
import com.magnitudestudios.GameFace.Network.FirebaseHelper

open class BaseActivity : AppCompatActivity() {
    @JvmField
    var firebaseHelper: FirebaseHelper? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        firebaseHelper = (applicationContext as GameFace).firebaseHelper
    }
}