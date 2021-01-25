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

/**
 * Friend container fragment
 *
 * @constructor Create empty Friend container fragment
 */
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

    /**
     * Profile tab adapter
     *
     * @constructor
     *
     * @param fragment
     */
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