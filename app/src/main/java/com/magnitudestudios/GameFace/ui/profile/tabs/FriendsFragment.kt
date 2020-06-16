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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.FragmentFriendsBinding
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.profile.ProfileViewModel
import com.magnitudestudios.GameFace.views.FriendViewHolder

class FriendsFragment : Fragment() {
    private lateinit var bind: FragmentFriendsBinding
    private lateinit var viewModel: ProfileViewModel

    private lateinit var mAdapter: SortedRVAdapter<Profile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(ProfileViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initAdapter()
        bind.friendsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = mAdapter
        }

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
        mAdapter.replaceAll(profiles)
    }

    private fun initAdapter() {
        mAdapter = object : SortedRVAdapter<Profile>(Profile::class.java) {
            override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return FriendViewHolder(RowFriendsBinding.inflate(inflater, parent, false))
            }

            override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
                val value = mAdapter.getitem(position)
                holder as FriendViewHolder
                Glide.with(holder.itemView.context).load("https://randomuser.me/api/portraits/med/men/75.jpg").circleCrop().into(holder.getImageView())
                holder.bind(value)
            }

            override fun areItemsSame(item1: Profile, item2: Profile): Boolean {
                return item1.uid == item2.uid
            }

            override fun compareItems(item1: Profile, item2: Profile): Int {
                return item1.username.compareTo(item2.username)
            }

        }
    }
}