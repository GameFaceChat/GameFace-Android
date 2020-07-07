/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentProfileBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendRequestsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.PersonalFragment

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
        bind.viewpager.adapter = ProfileTabAdapter(this)

        mainViewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it == null) bind.displayUsername.text = "Loading..."
            else {
                bind.displayUsername.text = it.data?.username
                bind.displayName.text = it.data?.name
                Glide.with(this).loadProfile(mainViewModel.profile.value?.data?.profilePic ?: "", bind.profilePic)
            }
        })

        bind.profilePic.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_takePhotoFragment)
        }

        bind.settingsBtn.setOnClickListener {
            activity?.findNavController(R.id.mainNavHost)?.navigate(R.id.action_bottomContainerFragment_to_settingsFragment)
        }

        TabLayoutMediator(bind.profileTabs, bind.viewpager) { tab, position ->
            when (position) {
                0 -> tab.icon = context?.getDrawable(R.drawable.ic_person)
                1 -> tab.icon = context?.getDrawable(R.drawable.ic_people_48px)
                2 -> tab.icon = context?.getDrawable(R.drawable.ic_mail)
                else -> throw IndexOutOfBoundsException("Index Out of Bounds At Profile Fragment Tabs: $position")
            }
        }.attach()

        //Observe for friend request changes
        observeFriends()

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

    class ProfileTabAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {
        override fun getItemCount(): Int = NUMBER_OF_TABS

        override fun createFragment(position: Int): Fragment {
            return when (position) {
                0 -> PersonalFragment()
                1 -> FriendsFragment()
                2 -> FriendRequestsFragment()
                else -> throw IndexOutOfBoundsException("Index Out of Bounds At Profile Fragment at Adapter: $position")
            }
        }
    }


}
