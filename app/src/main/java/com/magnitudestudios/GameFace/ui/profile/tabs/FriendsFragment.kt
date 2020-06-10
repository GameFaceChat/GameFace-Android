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
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.adapters.FriendsViewAdapter
import com.magnitudestudios.GameFace.databinding.FragmentFriendsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.ProfileViewModel

class FriendsFragment : Fragment() {
    private lateinit var bind: FragmentFriendsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(ProfileViewModel::class.java) }!!
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        bind.friendsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = FriendsViewAdapter()
        }
        mainViewModel.friends.observe(viewLifecycleOwner, Observer {
            if (it != null && it.isNotEmpty()) {
                viewModel.getFriendProfiles(it.keys.toMutableList())
            }
        })
        viewModel.friendProfiles.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                bind.noFriends.visibility = View.GONE
                bind.searchBar.visibility = View.VISIBLE
                bind.friendsList.visibility = View.VISIBLE
                populateRecyclerView(it)
            } else {
                bind.noFriends.visibility = View.VISIBLE
                bind.searchBar.visibility = View.GONE
                bind.friendsList.visibility = View.GONE
            }
        })
        viewModel.searchResultsFriends.observe(viewLifecycleOwner, Observer {
            if (it != null) populateRecyclerView(it)
        })
        bind.searchBar.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) viewModel.setQueryFriend(query)
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) viewModel.setQueryFriend(newText)
                return false
            }

        })
        bind.addFriendsBtn.setOnClickListener {
            parentFragment?.findNavController()?.navigate(R.id.action_profileFragment_to_addFriendsFragment)
        }

        super.onViewCreated(view, savedInstanceState)
    }

    private fun populateRecyclerView(profiles: List<Profile>) {
        (bind.friendsList.adapter as FriendsViewAdapter).replaceAll(profiles)
    }
}