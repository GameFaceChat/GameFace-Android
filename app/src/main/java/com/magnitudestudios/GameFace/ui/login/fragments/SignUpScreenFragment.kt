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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentSignupBinding
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.login.LoginViewModel


/**
 * Sign up screen fragment
 *
 * @constructor Create empty Sign up screen fragment
 */
class SignUpScreenFragment : Fragment(), View.OnClickListener {
    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentSignupBinding.inflate(inflater, container, false)
        viewModel = activity?.run {
            ViewModelProvider(this).get(LoginViewModel::class.java)
        }!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.signupButtonSignup.setOnClickListener(this)
        binding.signupCloseBtn.setOnClickListener(this)
        termsAndPrivacy()
    }

    private fun validateDetails(): Boolean {
        var valid = true
        if (!viewModel.validateEmail(binding.signupEmailInput.text.toString())) {
            binding.signupEmailInput.error = getString(R.string.enter_valid_email)
        } else {
            binding.signupEmailInput.error = null
        }
        if (binding.signupPasswordInput.text.toString().length <= 5) {
            valid = false
            binding.signupPasswordLayout.error = getString(R.string.pwd_length)
        } else {
            binding.signupPasswordLayout.error = null
        }
        if (binding.signupPasswordInput.text.toString() != binding.signupCPasswordInput.text.toString()) {
            valid = false
            binding.signupCPasswordLayout.error = getString(R.string.pwd_must_match)
        } else {
            binding.signupCPasswordLayout.error = null
        }
        return valid
    }

    private fun signUpUser(email: String, password: String) {
        binding.signupButtonSignup.setLoading(true)
        viewModel.signUpUserWithEmail(email, password).observe(this, Observer {
            if (it.status != Status.LOADING) binding.signupButtonSignup.setLoading(false)
            if (it.status == Status.SUCCESS && it.data!!) findNavController().navigate(R.id.action_signUpScreenFragment_to_finishSignUpFragment)
            else if (it.status == Status.ERROR) {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun termsAndPrivacy() {
        binding.signupTxvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.signupTxvTerms.highlightColor = Color.TRANSPARENT
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
        binding.signupTxvTerms.text = ss
    }

    override fun onClick(v: View) {
        when (v) {
            binding.signupButtonSignup -> if (validateDetails()) {
                signUpUser(binding.signupEmailInput.text.toString(), binding.signupPasswordInput.text.toString())
            }
            binding.signupCloseBtn -> findNavController().popBackStack()
        }
    }

    companion object {
        private const val TAG = "SignUpScreenFragment"
    }
}