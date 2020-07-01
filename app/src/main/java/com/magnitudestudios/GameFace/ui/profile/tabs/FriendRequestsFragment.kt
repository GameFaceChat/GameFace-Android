/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.callbacks.RVRequestButton
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.FragmentFriendRequestsBinding
import com.magnitudestudios.GameFace.databinding.RowRequestsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.profile.ProfileViewModel
import com.magnitudestudios.GameFace.views.FriendRequestViewHolder

class FriendRequestsFragment : Fragment() {
    private lateinit var bind: FragmentFriendRequestsBinding
    private lateinit var viewModel: ProfileViewModel
    private lateinit var requestAdapter: SortedRVAdapter<Profile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(ProfileViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //Adapter for friend requests
        requestAdapter = object : SortedRVAdapter<Profile>(Profile::class.java) {
            override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                return FriendRequestViewHolder(RowRequestsBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent, false),
                        object : RVRequestButton {
                            override fun acceptClicked(position: Int) {
                                viewModel.acceptFriendRequest(requestAdapter.getitem(position).uid)
                            }

                            override fun denyClicked(position: Int) {
                                viewModel.denyFriendRequest(requestAdapter.getitem(position).uid)
                            }

                        })
            }

            override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
                val value = this.getitem(position)
                (holder as FriendRequestViewHolder).bind(value)
                Glide.with(this@FriendRequestsFragment)
                        .load(value.profilePic)
                        .circleCrop()
                        .into(holder.getImageView())
            }

            override fun areItemsSame(item1: Profile, item2: Profile): Boolean {
                return item1.uid == item2.uid
            }

            override fun compareItems(item1: Profile, item2: Profile): Int {
                return (item2.getLastLogin()!! - item1.getLastLogin()!!).toInt()
            }
        }

        //Set up recyclerview
        bind.friendRequestList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = requestAdapter

        }

        viewModel.requestProfiles.observe(viewLifecycleOwner, Observer {
            if (it.isNullOrEmpty()) {
                bind.noFriendRequests.visibility = View.VISIBLE
                bind.friendRequestList.visibility = View.GONE
            } else {
                bind.noFriendRequests.visibility = View.GONE
                bind.friendRequestList.visibility = View.VISIBLE
            }
            Log.e("PROFILES: ", it.toString())
            requestAdapter.replaceAll(it)
        })

    }
}