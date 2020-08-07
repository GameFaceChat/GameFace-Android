/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FriendsContainerScreenBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel

class FriendContainerFragment : BaseFragment() {
    private lateinit var bind : FriendsContainerScreenBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: FriendsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FriendsContainerScreenBinding.inflate(inflater, container, false)
        mainViewModel = activity?.run {  ViewModelProvider(this).get(MainViewModel::class.java) }!!
        viewModel = activity?.run { ViewModelProvider(this).get(FriendsViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observeFriends()

        bind.viewpager.adapter = ProfileTabAdapter(this)
        bind.materialToolbar.setNavigationOnClickListener { findNavController().navigateUp() }
        TabLayoutMediator(bind.profileTabs, bind.viewpager) { tab, position ->
            when (position) {
                0 -> tab.icon = context?.getDrawable(R.drawable.ic_people_48px)
                1 -> tab.icon = context?.getDrawable(R.drawable.ic_mail)
                else -> throw IndexOutOfBoundsException("Index Out of Bounds At Profile Fragment Tabs: $position")
            }
        }.attach()

    }

    private fun observeFriends() {
        mainViewModel.friendRequests.observe(viewLifecycleOwner, Observer { it ->
            viewModel.setRequestUIDs(it.map { it.friendUID } as MutableList<String>)
        })

        mainViewModel.friends.observe(viewLifecycleOwner, Observer { friend ->
            if (friend != null) {
                viewModel.getFriendProfiles(friend.map { it.uid })
            }
        })

        viewModel.requestProfiles.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                bind.profileTabs.getTabAt(2)?.orCreateBadge?.number = it.size
            } else {
                bind.profileTabs.getTabAt(2)?.removeBadge()
            }
        })
    }

    inner class ProfileTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = NUMBER_OF_TABS

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> FriendsFragment()
                1 -> FriendRequestsFragment()
                else -> throw IndexOutOfBoundsException("Index Out of Bounds At Profile Fragment at Adapter: $position")
            }
        }
    }

    companion object {
        const val NUMBER_OF_TABS = 2
    }
}