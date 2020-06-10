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
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.tabs.TabLayoutMediator
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentProfileBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendRequestsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.PersonalFragment

class ProfileFragment : BaseFragment() {
    private lateinit var bind: FragmentProfileBinding

    private lateinit var mainViewModel: MainViewModel

    companion object {
        const val NUMBER_OF_TABS = 3
        private const val TAG = "ProfileFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentProfileBinding.inflate(inflater)
        mainViewModel = activity?.run {  ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.viewpager.adapter = ProfileTabAdapter(this)

        val url = "https://cdn.fastly.picmonkey.com/contentful/h6goo9gw1hh6/2sNZtFAWOdP1lmQ33VwRN3/24e953b920a9cd0ff2e1d587742a2472/1-intro-photo-final.jpg?w=1200&h=992&q=70&fm=webp"
        Glide.with(this).load(url)
                .placeholder(R.drawable.ic_add_profile_pic)
                .fallback(R.drawable.ic_add_profile_pic)
                .circleCrop()
                .into(bind.profilePic)
        mainViewModel.profile.observe(viewLifecycleOwner, Observer {
            if (it == null) bind.displayUsername.text = "Loading..."
            else bind.displayUsername.text = it.data?.username
        })

        TabLayoutMediator(bind.profileTabs, bind.viewpager) { tab, position ->
            when (position) {
                0 -> tab.icon = context?.getDrawable(R.drawable.ic_person)
                1 -> tab.icon = context?.getDrawable(R.drawable.ic_people_48px)
                2 -> tab.icon = context?.getDrawable(R.drawable.ic_mail)
                else -> throw IndexOutOfBoundsException("Index Out of Bounds At Profile Fragment Tabs: $position")
            }
        }.attach()


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
