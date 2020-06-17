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
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.ActivityMainBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.ui.Calling.IncomingCall
import com.magnitudestudios.GameFace.ui.login.LoginActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainActivity : BasePermissionsActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel = ViewModelProvider(this)[MainViewModel::class.java]
        viewModel.user.observe(this, Observer {
            if (it.status == Status.ERROR) Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            else if (it.status == Status.SUCCESS && it.data == null) goToLogin()
            else if (it.status == Status.SUCCESS && it.data != null) viewModel.checkDevice()
        })
        viewModel.profile.observe(this, Observer {
            if (it.status == Status.ERROR) Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
            else if (it.status == Status.SUCCESS && it.data == null)  {
                viewModel.signOutUser()
                Toast.makeText(this, getString(R.string.not_finished_setting_up), Toast.LENGTH_LONG).show()
            }
        })
//        goToCalling()
    }

    private fun goToLogin() {
        viewModel.signOutUser()
        Toast.makeText(this, "Signed Out", Toast.LENGTH_LONG).show()
        val i = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    private fun goToCalling() {
        val i = Intent(this@MainActivity, IncomingCall::class.java)
        startActivity(i)
        finish()
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}