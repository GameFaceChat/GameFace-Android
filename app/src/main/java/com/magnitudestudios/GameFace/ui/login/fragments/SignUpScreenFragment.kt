/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login.fragments

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
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentSignupBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.ui.login.LoginViewModel

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

    }

    private fun validateDetails(): Boolean {
        var valid = true
        if (!binding.signupEmailInput.text.toString().contains("@") || !binding.signupEmailInput.text.toString().contains(".")) {
            binding.signupEmailInput.error = getString(R.string.enter_valid_email)
        }
        if (binding.signupPasswordInput.text.toString().length <= 5) {
            valid = false
            binding.signupPasswordLayout.error = getString(R.string.pwd_length)
        }
        if (binding.signupPasswordInput.text.toString() != binding.signupCPasswordInput.text.toString()) {
            valid = false
            binding.signupCPasswordLayout.error = getString(R.string.pwd_must_match)
        }
        return valid
    }

    private fun signUpUser(email: String, password: String) {
        viewModel.signUpUserWithEmail(email, password).observe(this, Observer {
            if (it.status == Status.SUCCESS && it.data!!) findNavController().navigate(R.id.action_signUpScreenFragment_to_finishSignUpFragment)
            else if (it.status == Status.ERROR) Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
        })
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