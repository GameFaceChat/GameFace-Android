/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentLoginBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.ui.login.LoginViewModel

class LoginScreenFragment : Fragment(), View.OnClickListener {
    private var mGoogleSignInClient: GoogleSignInClient? = null

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        viewModel = activity?.run {
            ViewModelProvider(this).get(LoginViewModel::class.java)
        }!!
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oAuth_client_ID))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.loginBtnSignup.setOnClickListener(this)
        binding.loginSignButton.setOnClickListener(this)
        binding.loginCardSigninwithgoogle.setOnClickListener(this)
        binding.forgotPassword.setOnClickListener(this)
        binding.loginCloseBtn.setOnClickListener(this)

        viewModel.authenticated.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.LOADING) binding.loginSignButton.setLoading(true)
            else binding.loginSignButton.setLoading(false)
        })
    }

    private fun validate(): Boolean {
        var valid = true
        val email = binding.loginEmailInput.text.toString()
        val password = binding.loginPasswordInput.text.toString()
        if (!viewModel.validateEmail(email)) {
            binding.loginEmailInput.error = getString(R.string.enter_valid_email)
            valid = false
        }
        if (password.isEmpty()) {
            binding.loginPasswordLayout.error = getString(R.string.pwd_length)
            valid = false
        }
        return valid
    }

    private fun onClickSignWithGoogle() {
        val signInIntent = mGoogleSignInClient!!.signInIntent
        startActivityForResult(signInIntent, GOOGLE_RESULT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == GOOGLE_RESULT) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)
                viewModel.firebaseAuthWithGoogle(account).observe(this@LoginScreenFragment, Observer {
                    if (it.status == Status.SUCCESS && !it.data!!) findNavController().navigate(R.id.action_loginScreenFragment_to_finishSignUpFragment)
                })
            } catch (e: ApiException) {
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(context, e.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun sendForgotPassword() {
        val emailAddress = binding.loginEmailInput.text.toString()
        if (viewModel.validateEmail(emailAddress)) {
            viewModel.sendForgotPassword(emailAddress).observe(this, Observer {
                when (it.status) {
                    Status.SUCCESS -> Toast.makeText(context, getString(R.string.pwd_reset_email_sent, emailAddress), Toast.LENGTH_SHORT).show()
                    Status.ERROR -> Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
                    else -> Log.e(TAG, "Unknown Status " + it.status.name)
                }
            })
        } else {
            binding.loginEmailInput.error = getString(R.string.enter_valid_email)
        }
    }


    override fun onClick(v: View) {
        when (v) {
            binding.loginSignButton -> if (validate()) {
                viewModel.signInWithEmail(binding.loginEmailInput.text.toString(), binding.loginPasswordInput.text.toString())
            }
            binding.loginCardSigninwithgoogle -> onClickSignWithGoogle()
            binding.forgotPassword -> sendForgotPassword()
            binding.loginCloseBtn -> findNavController().popBackStack()
        }
    }

    companion object {
        private const val TAG = "LoginScreenFragment"
        private const val GOOGLE_RESULT = 101
    }
}