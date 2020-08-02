/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.main

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityMainBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.BottomContainerFragmentDirections
import com.magnitudestudios.GameFace.ui.login.LoginActivity


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
                goToLogin(getString(R.string.not_finished_setting_up))
            }
        })
        if (savedInstanceState == null) checkIntent(intent)
        Log.e("VERSION", " " + Integer.valueOf(android.os.Build.VERSION.SDK_INT))
    }

    private fun checkIntent(intent: Intent?) {
        val extras = intent?.extras
        if (extras != null && extras.containsKey(Constants.CALL_KEY) && extras.containsKey(Constants.ROOM_ID_KEY)) {
            Log.e("HERE", intent.getStringExtra(Constants.ROOM_ID_KEY)!!)
            val action = BottomContainerFragmentDirections
                    .actionBottomContainerFragmentToCameraFragment("", extras.getString(Constants.ROOM_ID_KEY) ?: "")
            findNavController(R.id.mainNavHost).navigate(action)
        }
    }

    override fun onResume() {
        super.onResume()
        //Cancel Notifications
        val nMgr = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nMgr.cancelAll()
    }

    private fun goToLogin(text: String = "Signed Out") {
        viewModel.signOutUser()
        cacheDir.deleteRecursively()
        Toast.makeText(this, text, Toast.LENGTH_LONG).show()
        val i = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(i)
        finish()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        checkIntent(intent)
    }

    companion object {
        private const val TAG = "MainActivity"
    }
}