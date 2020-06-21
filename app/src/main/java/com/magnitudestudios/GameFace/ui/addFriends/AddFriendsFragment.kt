/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.addFriends

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.FragmentAddFriendsBinding
import com.magnitudestudios.GameFace.databinding.RowUsersBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.views.AddFriendViewHolder

class AddFriendsFragment : Fragment() {
    private lateinit var bind: FragmentAddFriendsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: AddFriendsViewModel

    private lateinit var addAdapter: SortedRVAdapter<Profile>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentAddFriendsBinding.inflate(inflater, container, false)
        viewModel = requireParentFragment().run { ViewModelProvider(this).get(AddFriendsViewModel::class.java) }
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!viewModel.getQueryString().isNullOrEmpty()) bind.searchBarLayout.searchEditText.setText(viewModel.getQueryString())
        bind.doneBtn.setOnClickListener { findNavController().popBackStack() }

        initAdapter()

        bind.usersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addAdapter
        }

        viewModel.results.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.SUCCESS && it.data != null) {
                addAdapter.replaceAll(it.data)
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        })

        viewModel.friendRequestedUIDs.observe(viewLifecycleOwner, Observer { redrawList() })

        mainViewModel.friendRequestsSent.observe(viewLifecycleOwner, Observer { viewModel.setFriendRequestsSent(it) })
        mainViewModel.friends.observe(viewLifecycleOwner, Observer { viewModel.setFriends(it) })

        bind.searchBarLayout.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setQueryString(s.toString())
            }
        })

    }

    private fun redrawList() {
        bind.usersList.adapter = null
        bind.usersList.adapter = addAdapter
    }

    private fun initAdapter() {
        addAdapter = object : SortedRVAdapter<Profile>(Profile::class.java) {
            override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return AddFriendViewHolder(RowUsersBinding.inflate(inflater, parent, false), object : RVButtonClick {
                    override fun onClick(position: Int) {
                        val clicked = addAdapter.getitem(position)
                        if (getHolderState(clicked.uid) == Constants.STATE_DEFAULT) viewModel.sendFriendRequest(clicked)
                        else if (getHolderState(clicked.uid) == Constants.STATE_REQUESTED) viewModel.deleteFriendRequest(clicked)
                    }
                    override fun onLongClick(position: Int) {}

                })
            }

            override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
                val value = addAdapter.getitem(position)
                holder as AddFriendViewHolder
                Glide.with(holder.itemView.context).load("https://picsum.photos/300/300").circleCrop().into(holder.getImageView())
                holder.bind(value)
                holder.setState(getHolderState(value.uid))
            }

            override fun areItemsSame(item1: Profile, item2: Profile): Boolean { return item1.uid == item2.uid }

            override fun compareItems(item1: Profile, item2: Profile): Int { return item1.username.compareTo(item2.username) }

            private fun getHolderState(uid: String) : Int {
                return when {
                    viewModel.getFriendRequestedUIDs().contains(uid) -> Constants.STATE_REQUESTED        //Already Requested
                    viewModel.getFriendUIDs().contains(uid) -> Constants.STATE_FRIENDS                   //Friends
                    mainViewModel.user.value?.data?.uid == uid -> Constants.STATE_OWN_PROFILE            //Is current user
                    else -> Constants.STATE_DEFAULT
                }
            }

        }
    }
}