/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.magnitudestudios.GameFace.ui.main.MainActivity
import com.magnitudestudios.GameFace.callbacks.UserLoginListener
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), UserLoginListener {
    private var mAuth: FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mAuth = FirebaseAuth.getInstance()
    }

    override fun onStart() {
        super.onStart()
        val currentUser = mAuth!!.currentUser
        if (currentUser != null) {
            goToMainActivity()
        }
    }


    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun signedInUser() {
        goToMainActivity()
    }

    override fun onBackPressed() {
        findNavController(R.id.login_frame_replace).navigateUp()
    }
}