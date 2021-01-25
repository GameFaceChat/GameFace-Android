/*
 * Copyright (c) 2021 -Srihari Vishnu - All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.magnitudestudios.GameFace.ui.login

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BasePermissionsActivity
import com.magnitudestudios.GameFace.databinding.ActivityLoginBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.main.MainActivity


/**
 * Login activity
 *
 * @constructor Create empty Login activity
 */
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
//            if (it.status == Status.LOADING) binding.progressBar.visibility = View.VISIBLE
//            else binding.progressBar.visibility = View.GONE
            //Error
            if (it.status == Status.ERROR) {
                Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                viewModel.resetUserStatus()
            }
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