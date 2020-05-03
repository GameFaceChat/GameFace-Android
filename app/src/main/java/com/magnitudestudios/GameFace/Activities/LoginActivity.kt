package com.magnitudestudios.GameFace.Activities

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.magnitudestudios.GameFace.Fragments.Login.LoginScreenFragment
import com.magnitudestudios.GameFace.Fragments.Login.SignUpScreenFragment
import com.magnitudestudios.GameFace.Fragments.Login.StartScreenFragment
import com.magnitudestudios.GameFace.GameFace
import com.magnitudestudios.GameFace.Interfaces.UserLoginListener
import com.magnitudestudios.GameFace.Network.FirebaseHelper
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity(), UserLoginListener {
    private var mAuth: FirebaseAuth? = null
    private lateinit var binding: ActivityLoginBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
//        switchFragment(StartScreenFragment())
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
        FirebaseHelper.getUsername()

        goToMainActivity()
    }

    override fun onBackPressed() {}
}