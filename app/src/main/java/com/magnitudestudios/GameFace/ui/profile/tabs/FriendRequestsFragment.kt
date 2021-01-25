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
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.callbacks.RVRequestButton
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.FragmentFriendRequestsBinding
import com.magnitudestudios.GameFace.databinding.RowRequestsBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.views.holders.FriendRequestViewHolder

/**
 * Friend requests fragment
 *
 * @constructor Create empty Friend requests fragment
 */
class FriendRequestsFragment : Fragment() {
    private lateinit var bind: FragmentFriendRequestsBinding
    private lateinit var viewModel: FriendsViewModel
    private lateinit var requestAdapter: SortedRVAdapter<Profile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(FriendsViewModel::class.java) }!!
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
                Glide.with(this@FriendRequestsFragment).loadProfile(value.profilePic, holder.getImageView())
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