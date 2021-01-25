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

package com.magnitudestudios.GameFace.ui.camera.addMembers

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.AddMembersDialogBinding
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.camera.CameraViewModel
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.views.holders.FriendViewHolder

/**
 * Add members dialog
 *
 * @constructor Create empty Add members dialog
 */
class AddMembersDialog : BottomSheetDialogFragment() {
    private lateinit var bind : AddMembersDialogBinding
    private lateinit var mainViewModel: MainViewModel
    private val viewModel: CameraViewModel by navGraphViewModels(R.id.videoCallGraph)
    private lateinit var membersModel: AddMembersViewModel

    private lateinit var friendsAdapter : SortedRVAdapter<Profile>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = AddMembersDialogBinding.inflate(inflater, container, false)
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        membersModel = ViewModelProvider(this).get(AddMembersViewModel::class.java)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        friendsAdapter = object : SortedRVAdapter<Profile>(Profile::class.java) {
            override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return FriendViewHolder(RowFriendsBinding.inflate(inflater, parent, false), object : RVButtonClick {
                    override fun onClick(position: Int) {
                        viewModel.addMember(getitem(position).uid)
                        dismiss()
                    }

                    override fun onLongClick(position: Int) {}
                })
            }

            override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
                val value = getitem(position)
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
        bind.callFriendsList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = friendsAdapter
        }
        membersModel.friends.value = mainViewModel.friends.value
        mainViewModel.friends.observe(viewLifecycleOwner, Observer {
            membersModel.friends.value = it
            Log.e("GOT FRIENDS", it.toString())
        })

        membersModel.friendProfiles.observe(viewLifecycleOwner, Observer {
            friendsAdapter.replaceAll(it)
        })
    }
}