/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.CardPackBinding
import com.magnitudestudios.GameFace.databinding.FragmentShopBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.ui.profile.ProfileFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendRequestsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.FriendsFragment
import com.magnitudestudios.GameFace.ui.profile.tabs.PersonalFragment
import com.magnitudestudios.GameFace.ui.shop.tabs.MarketFragment
import com.magnitudestudios.GameFace.views.CardPackViewHolder

class ShopFragment : BaseFragment() {

    lateinit var bind:FragmentShopBinding
    val viewModel: ShopViewModel by navGraphViewModels(R.id.bottom_nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentShopBinding.inflate(inflater)
//        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bind.shopTabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {}

            override fun onTabUnselected(tab: TabLayout.Tab?) {}

            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab?.position != null) replaceFragment(tab.position)
            }

        })
        replaceFragment(0)
    }

    fun replaceFragment(position: Int) {
        val fragment = when (position) {
            0 -> MarketFragment()
//            1 -> FriendsFragment()
//            2 -> FriendRequestsFragment()
            else -> MarketFragment()
        }
        childFragmentManager.beginTransaction().replace(R.id.shopContainer, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }
}