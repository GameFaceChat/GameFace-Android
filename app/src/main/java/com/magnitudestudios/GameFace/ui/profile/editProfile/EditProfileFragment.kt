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

package com.magnitudestudios.GameFace.ui.profile.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.DialogEditProfileBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.ui.main.MainViewModel

/**
 * Edit profile fragment
 *
 * @constructor Create empty Edit profile fragment
 */
class EditProfileFragment : BottomSheetDialogFragment() {
    private lateinit var bind: DialogEditProfileBinding
    private lateinit var viewModel : EditProfileViewModel
    private lateinit var mainViewModel : MainViewModel
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = DialogEditProfileBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(this).get(EditProfileViewModel::class.java)
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val usernameOriginal = mainViewModel.profile.value?.data?.username ?: ""
        val bioOriginal = mainViewModel.profile.value?.data?.bio ?: ""
        val nameOriginal = mainViewModel.profile.value?.data?.name ?: ""
        //Init Text Values With Current Values
        bind.username.setText(usernameOriginal)
        bind.name.setText(nameOriginal)
        bind.bio.setText(bioOriginal)
        bind.saveBtn.isEnabled = false

        //Update Viewmodel
        viewModel.setUsername(usernameOriginal)
        viewModel.setOriginalName(nameOriginal)
        viewModel.setOriginalBio(bioOriginal)

        //Add text change listeners
        bind.name.addTextChangedListener {
            viewModel.setName(it.toString())
        }

        bind.bio.addTextChangedListener {
            viewModel.setBio(it.toString())
        }

        viewModel.changed.observe(viewLifecycleOwner, Observer {
            bind.saveBtn.isEnabled = it
        })

        viewModel.savingProgress.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.LOADING) bind.progressBar.visibility = View.VISIBLE
            else bind.progressBar.visibility = View.GONE
            if (it.status == Status.ERROR) Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            if (it.status == Status.SUCCESS) {
                activity?.findNavController(R.id.mainNavHost)?.navigateUp()
                mainViewModel.profile.value  = Resource.success(mainViewModel.profile.value?.data?.apply {
                    name = viewModel.getName()
                    bio = viewModel.getBio()
                })
                Toast.makeText(context, "Saved Profile", Toast.LENGTH_SHORT).show()
            }
        })

        bind.profilePic.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_editProfileFragment_to_takePhotoFragment)
        }
        bind.changePfp.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_editProfileFragment_to_takePhotoFragment)
        }
        Glide.with(this).loadProfile(mainViewModel.profile.value?.data?.profilePic ?: "", bind.profilePic)
        //Finish
        bind.cancelBtn.setOnClickListener { dismiss() }
        bind.saveBtn.setOnClickListener { viewModel.save() }
    }
}