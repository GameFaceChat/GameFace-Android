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
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentShopBinding
import com.magnitudestudios.GameFace.databinding.ItemShowcaseBinding
import com.magnitudestudios.GameFace.ui.BottomContainerFragmentDirections
import com.magnitudestudios.GameFace.ui.shop.tabs.InstalledFragment
import com.magnitudestudios.GameFace.ui.shop.tabs.MarketFragment
import com.magnitudestudios.GameFace.views.ShowCaseItemViewHolder
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

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

        viewModel.showcaseItems.observe(viewLifecycleOwner, Observer {
            bind.showcaseFlipper.adapter = ShowCaseAdapter()
        })

        lifecycleScope.launchWhenCreated{
            while (isActive) {
                delay(5000)
                bind.showcaseFlipper.currentItem = (bind.showcaseFlipper.currentItem + 1) % (viewModel.showcaseItems.value?.size ?: 1)
            }
        }

        bind.infoBtn.setOnClickListener {
            Log.e("GEttING", "ITEM1")
            if (viewModel.showcaseItems.value == null) return@setOnClickListener
            Log.e("GEttING", "ITEM")
            viewModel.selectedShowcaseItem.value = viewModel.showcaseItems.value!![bind.showcaseFlipper.currentItem]
        }

        viewModel.selectedShowcase.observe(viewLifecycleOwner, Observer {
            Log.e("GOT", "SHOPITEM: ${it?.name}")
            if (it == null) return@Observer
            viewModel.selectedShowcaseItem.value = null
            val clickedItem = Gson().toJson(it)
            val action = BottomContainerFragmentDirections.actionBottomContainerFragmentToCardPackDetailsFragment(it.imgURL, clickedItem)
            try {
                activity?.findNavController(R.id.mainNavHost)?.navigate(action)
            } catch (e: Exception){Log.e("SELECTEDSHOWCASE", e.message, e)}
        })

    }

    fun replaceFragment(position: Int) {
        val fragment = when (position) {
            0 -> MarketFragment()
            1 -> InstalledFragment()
//            1 -> FriendsFragment()
//            2 -> FriendRequestsFragment()
            else -> MarketFragment()
        }
        childFragmentManager.beginTransaction().replace(R.id.shopContainer, fragment).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE).commit()
    }

    inner class ShowCaseAdapter : RecyclerView.Adapter<ShowCaseItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCaseItemViewHolder {
            return ShowCaseItemViewHolder(ItemShowcaseBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int {
            return viewModel.showcaseItems.value?.size ?: 0
        }

        override fun onBindViewHolder(holder: ShowCaseItemViewHolder, position: Int) {
            holder.bind(viewModel.showcaseItems.value!![position])
        }
    }
}