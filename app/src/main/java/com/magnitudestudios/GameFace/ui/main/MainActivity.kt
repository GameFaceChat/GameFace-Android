/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ActivityMainBinding
import com.magnitudestudios.GameFace.network.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.Status
import com.magnitudestudios.GameFace.ui.login.LoginActivity


class MainActivity : BasePermissionsActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.user.observe(this, Observer {
            if (it.status == Status.ERROR) goToLogin()
        })
        val navhost = findNavController(R.id.mainNavHost)
        binding.mainBottomNav.setupWithNavController(navhost)
    }

    private fun goToLogin() {
        Toast.makeText(this, getString(R.string.error_fetching_user), Toast.LENGTH_LONG).show()
        val i = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}