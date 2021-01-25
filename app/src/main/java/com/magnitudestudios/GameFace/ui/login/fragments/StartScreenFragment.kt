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

package com.magnitudestudios.GameFace.ui.login.fragments

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentStartscreenBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.login.LoginViewModel

/**
 * Start screen fragment
 *
 * @constructor Create empty Start screen fragment
 */
class StartScreenFragment : Fragment(), View.OnClickListener {
    private var mGoogleSignInClient: GoogleSignInClient? = null
    private lateinit var navController: NavController
    private lateinit var binding: FragmentStartscreenBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentStartscreenBinding.inflate(inflater, container, false)
        viewModel = activity?.run {
            ViewModelProvider(this).get(LoginViewModel::class.java)
        }!!

        binding.startscreenCardSignupwithgoogle.setOnClickListener(this)
        binding.startscreenCardSignupwithemail.setOnClickListener(this)
        binding.startscreenGottologin.setOnClickListener(this)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.google_oAuth_client_ID))
                .requestEmail()
                .build()
        mGoogleSignInClient = GoogleSignIn.getClient(requireContext(), gso)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        termsAndPrivacy()
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
                viewModel.firebaseAuthWithGoogle(account).observe(this@StartScreenFragment, Observer {
                    if (it.status == Status.SUCCESS && !it.data!!) findNavController().navigate(R.id.action_startScreenFragment_to_finishSignUpFragment)
                })
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
                Toast.makeText(context, "Sign in failed", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun termsAndPrivacy() {
        binding.startscreenTxvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.startscreenTxvTerms.highlightColor = Color.TRANSPARENT
        val ss = SpannableString(getString(R.string.terms_and_conditions_text))
        val termsConditions: ClickableSpan = object : ClickableSpan() {
            override fun onClick(textView: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.termsAndConditionsLink))))
            }
        }
        val privacyPolicy = object : ClickableSpan() {
            override fun onClick(widget: View) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.privacyPolicyLink))))
            }
        }
        ss.setSpan(termsConditions, ss.indexOf("Terms"), ss.indexOf("Conditions")+10, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        ss.setSpan(privacyPolicy, ss.indexOf("Privacy"), ss.indexOf("Policy") + 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        binding.startscreenTxvTerms.text = ss
    }

    override fun onClick(v: View) {
        when (v) {
            binding.startscreenCardSignupwithgoogle -> onClickSignWithGoogle()
            binding.startscreenCardSignupwithemail -> navController.navigate(R.id.action_startScreenFragment_to_signUpScreenFragment)
            binding.startscreenGottologin -> navController.navigate(R.id.action_startScreenFragment_to_loginScreenFragment)
        }
    }

    companion object {
        private const val TAG = "StartScreenFragment"
        const val GOOGLE_RESULT = 101
    }
}