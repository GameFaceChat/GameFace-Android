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
import com.magnitudestudios.GameFace.adapters.UsersViewAdapter
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.FragmentAddFriendsBinding
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.ProfileViewModel

class AddFriendsFragment : Fragment() {
    private lateinit var bind: FragmentAddFriendsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: AddFriendsViewModel

    private lateinit var addAdapter: UsersViewAdapter

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
        addAdapter = UsersViewAdapter(object : RVButtonClick {
            override fun onClick(position: Int) {
                val clicked = addAdapter.getitem(position)

                if (!addAdapter.getRequestedFriends().contains(clicked.uid)) viewModel.sendFriendRequest(clicked)
            }

            override fun onLongClick(position: Int) {}
        })

        bind.usersList.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = addAdapter
        }

        viewModel.results.observe(viewLifecycleOwner, Observer {
            if (it.status == Status.SUCCESS && it.data != null) {
                (bind.usersList.adapter as UsersViewAdapter).replaceAll(it.data)
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_LONG).show()
            }
        })

        mainViewModel.friendRequestsSent.observe(viewLifecycleOwner, Observer {
            if (it != null) {
                (bind.usersList.adapter as UsersViewAdapter).setRequestedFriends(it)
                bind.usersList.adapter = null
                bind.usersList.adapter = addAdapter
            }
        })

        bind.searchBarLayout.searchEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.setQueryString(s.toString())
            }
        })

    }
}