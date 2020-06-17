/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login.fragments

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.FragmentFinishSigningUpBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.ui.login.LoginViewModel
import java.util.*

class FinishSignUpFragment : Fragment() {
    private lateinit var binding: FragmentFinishSigningUpBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = FragmentFinishSigningUpBinding.inflate(layoutInflater, container, false)
        viewModel = activity?.run {
            ViewModelProvider(this).get(LoginViewModel::class.java)
        }!!
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnFinishSignup.setOnClickListener {
            if (validateDetails() && viewModel.usernameExists.value?.data == false) {
                viewModel.createUser(binding.signupUsernameInput.text.toString(), binding.fullNameInput.text.toString(),
                 binding.bioInput.text.toString())
            }
        }

        binding.profilePic.setOnClickListener {
        }

        binding.signupUsernameInput.doAfterTextChanged {text ->
            if (validateDetails()) viewModel.userNameExists(text.toString())
        }

        viewModel.usernameExists.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.SUCCESS && it.data!!) binding.signupUsernameInput.error = getString(R.string.username_exists)
        })

    }

    private fun validateDetails(): Boolean {
        var flag = true
        val usernameText = binding.signupUsernameInput.text.toString()
        when {
            usernameText.length < 6 -> {
                binding.signupUsernameInput.error = getString(R.string.username_length)
                flag = false
            }
            usernameText.contains(" ") -> {
                binding.signupUsernameInput.error = getString(R.string.username_no_spaces)
                flag = false
            }
            usernameSwearWords(usernameText) -> {
                binding.signupUsernameInput.error = getString(R.string.username_bad_phrase)
                flag = false
            }
        }

        return flag
    }

    private fun usernameSwearWords(username: String) : Boolean {
        for (a in resources.getStringArray(R.array.array_bad_words)) {
            if (username.contains(a)) return true
        }
        return false
    }


}