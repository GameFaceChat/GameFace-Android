/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.AddMembersDialogBinding
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.camera.CameraViewModel
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.views.FriendViewHolder

class AddMembersDialog : BottomSheetDialogFragment() {
    private lateinit var bind : AddMembersDialogBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: CameraViewModel
    private lateinit var membersModel: AddMembersViewModel

    private lateinit var friendsAdapter : SortedRVAdapter<Profile>
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = AddMembersDialogBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(CameraViewModel::class.java)}!!
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