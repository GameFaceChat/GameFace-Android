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
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.magnitudestudios.GameFace.databinding.FragmentFriendRequestsBinding
import com.magnitudestudios.GameFace.ui.main.MainViewModel
import com.magnitudestudios.GameFace.ui.profile.ProfileViewModel

class FriendRequestsFragment : Fragment() {
    private lateinit var bind: FragmentFriendRequestsBinding
    private lateinit var mainViewModel: MainViewModel
    private lateinit var viewModel: ProfileViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentFriendRequestsBinding.inflate(inflater, container, false)
        viewModel = activity?.run { ViewModelProvider(this).get(ProfileViewModel::class.java) }!!
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}