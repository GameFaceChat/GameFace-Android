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
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.FragmentFriendsBinding
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.views.holders.FriendViewHolder

/**
 * Friends fragment
 *
 * @constructor Create empty Friends fragment
 */
class FriendsFragment : Fragment() {
    private lateinit var bind: FragmentFriendsBinding
    private lateinit var viewModel: FriendsViewModel

    private lateinit var mAdapter: SortedRVAdapter<Profile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(FriendsViewModel::class.java) }!!
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
            requireActivity().findNavController(R.id.mainNavHost).navigate(R.id.action_friendContainerFragment_to_addFriendsFragment)
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
                return FriendViewHolder(RowFriendsBinding.inflate(inflater, parent, false), object : RVButtonClick {
                    override fun onClick(position: Int) {
                        val action = FriendContainerFragmentDirections.actionFriendContainerFragmentToVideoCallGraph(mAdapter.getitem(position).uid)
                        activity?.findNavController(R.id.mainNavHost)?.navigate(action)
                    }

                    override fun onLongClick(position: Int) {}

                })
            }

            override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
                val value = mAdapter.getitem(position)
                holder as FriendViewHolder
                Glide.with(holder.itemView.context).loadProfile(value.profilePic, holder.getImageView())
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