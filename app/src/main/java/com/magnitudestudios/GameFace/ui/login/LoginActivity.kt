/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.firebase.auth.FirebaseAuth
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityLoginBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.ui.main.MainActivity


class LoginActivity : BasePermissionsActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkPlayServices()

        if (intent.getBooleanExtra(Constants.NOT_FINISHED, false)) {
            findNavController(R.id.login_frame_replace).navigate(R.id.finishSignUpFragment)
        }
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        viewModel.authenticated.observe(this, Observer {
            //Fully Authenticated
            if (it.status == Status.SUCCESS && it.data!!) goToMainActivity()
            //Loading
            if (it.status == Status.LOADING) binding.progressBar.visibility = View.VISIBLE
            else binding.progressBar.visibility = View.GONE
            //Error
            if (it.status == Status.ERROR) Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
        })
    }

    private fun goToMainActivity() {
        val intent = Intent(this@LoginActivity, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (viewModel.isFirebaseUserNull()) findNavController(R.id.login_frame_replace).navigateUp()
    }

    private fun checkPlayServices() {
        val gApi = GoogleApiAvailability.getInstance()
        val resultCode = gApi.isGooglePlayServicesAvailable(this)
        if (resultCode != ConnectionResult.SUCCESS) {
            if (gApi.isUserResolvableError(resultCode)) {
                gApi.getErrorDialog(this, resultCode, 2404).show()
            } else {
                Toast.makeText(this, resources.getString(R.string.toast_playservices_unrecoverable), Toast.LENGTH_LONG).show()
                GoogleApiAvailability.getInstance().makeGooglePlayServicesAvailable(this)
            }
            finish()
        }
    }
}