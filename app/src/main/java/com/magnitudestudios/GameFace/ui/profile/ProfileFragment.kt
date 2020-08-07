/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.motion.widget.MotionLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayoutMediator
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentProfileBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendRequestsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendsFragment

class ProfileFragment : BaseFragment() {
    private lateinit var bind: FragmentProfileBinding

    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: ProfileViewModel

    companion object {
        const val NUMBER_OF_TABS = 3
        private const val TAG = "ProfileFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentProfileBinding.inflate(inflater)
        mainViewModel = activity?.run {  ViewModelProvider(this).get(MainViewModel::class.java) }!!
        viewModel = activity?.run { ViewModelProvider(this).get(ProfileViewModel::class.java) }!!
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainViewModel.profile.observe(viewLifecycleOwner, Observer {
            bind.displayUsername.text = it.data?.username ?: ""
            bind.displayName.text = it.data?.name ?: ""
            bind.displayBio.text = it.data?.bio ?: ""
            Glide.with(this).loadProfile(mainViewModel.profile.value?.data?.profilePic ?: "", bind.profilePic)
        })

        mainViewModel.friends.observe(viewLifecycleOwner, Observer {
            bind.statsLayout.displayFriends.text = it.size.toString()
        })

        viewModel.numberOfInstalledPacks.observe(viewLifecycleOwner, Observer {
            bind.statsLayout.displayPacks.text = it.toString()
        })

        bind.profilePic.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_editProfileFragment)
        }

        bind.settingsBtn.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_settingsFragment)
        }

        bind.editProfile.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_editProfileFragment)
        }

        bind.statsLayout.displayFriends.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_friendContainerFragment)
        }

        bind.root.addTransitionListener(object : MotionLayout.TransitionListener {
            override fun onTransitionTrigger(p0: MotionLayout?, p1: Int, p2: Boolean, p3: Float) {}

            override fun onTransitionStarted(p0: MotionLayout?, p1: Int, p2: Int) {}

            override fun onTransitionChange(p0: MotionLayout?, p1: Int, p2: Int, p3: Float) {
                bind.statsLayout.displayFriends.isEnabled = p3 <= 0.5f
            }

            override fun onTransitionCompleted(p0: MotionLayout?, p1: Int) {}

        })

    }


}
