/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile.editProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.databinding.DialogEditProfileBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel

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
        val bioOriginal = mainViewModel.profile.value?.data?.bio ?: ""
        val nameOriginal = mainViewModel.profile.value?.data?.name ?: ""
        //Init Text Values With Current Values
        bind.username.setText(mainViewModel.profile.value?.data?.username)
        bind.name.setText(nameOriginal)
        bind.bio.setText(bioOriginal)
        bind.saveBtn.isEnabled = false

        //Update Viewmodel
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

        bind.profilePic.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_editProfileFragment_to_takePhotoFragment)
        }
        //Finish
        bind.cancelBtn.setOnClickListener { dismiss() }
        bind.saveBtn.setOnClickListener { viewModel.save() }
    }
}