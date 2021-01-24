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

/**
 * Profile fragment
 *
 * @constructor Create empty Profile fragment
 */
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
