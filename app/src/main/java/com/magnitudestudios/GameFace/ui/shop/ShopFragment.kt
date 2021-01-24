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
import com.google.android.material.tabs.TabLayoutMediator
import com.google.gson.Gson
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.FragmentShopBinding
import com.magnitudestudios.GameFace.databinding.ItemShowcaseBinding
import com.magnitudestudios.GameFace.ui.BottomContainerFragmentDirections
import com.magnitudestudios.GameFace.ui.shop.tabs.InstalledFragment
import com.magnitudestudios.GameFace.ui.shop.tabs.MarketFragment
import com.magnitudestudios.GameFace.views.ShowCaseItemViewHolder
import kotlinx.coroutines.*

/**
 * Shop fragment
 *
 * @constructor Create empty Shop fragment
 */
class ShopFragment : BaseFragment() {

    lateinit var bind:FragmentShopBinding
    val viewModel: ShopViewModel by navGraphViewModels(R.id.bottom_nav_graph)

    var repeatedSwitch : Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentShopBinding.inflate(inflater)
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
            TabLayoutMediator(bind.tabDots, bind.showcaseFlipper) {_, _ -> }.attach()
        })

        repeatedSwitch = lifecycleScope.launch {
            Log.e("LAUNCHED", "COROUTINE")
            while (isActive) {
                delay(5000)
                bind.showcaseFlipper.currentItem = (bind.showcaseFlipper.currentItem + 1) % (viewModel.showcaseItems.value?.size ?: 1)
            }
        }

        viewModel.selectedShowcase.observe(viewLifecycleOwner, Observer {
            if (it == null) return@Observer
            viewModel.selectedShowcaseItem.value = null
            val clickedItem = Gson().toJson(it)
            val action = BottomContainerFragmentDirections.actionBottomContainerFragmentToCardPackDetailsFragment(it.imgURL, clickedItem)
            try {
                activity?.findNavController(R.id.mainNavHost)?.navigate(action)
            } catch (e: Exception){Log.e("SELECTED_SHOWCASE", e.message, e)}
        })

    }

    override fun onPause() {
        super.onPause()
        repeatedSwitch?.cancel()
    }

    override fun onResume() {
        super.onResume()
        if (repeatedSwitch?.isCancelled == true) repeatedSwitch?.start()
    }

    /**
     * Replace fragment
     *
     * @param position
     */
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

    /**
     * Show case adapter
     *
     * @constructor Create empty Show case adapter
     */
    inner class ShowCaseAdapter : RecyclerView.Adapter<ShowCaseItemViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShowCaseItemViewHolder {
            return ShowCaseItemViewHolder(ItemShowcaseBinding.inflate(LayoutInflater.from(parent.context), parent, false),
                object : RVButtonClick {
                    override fun onClick(position: Int) {
                        if (viewModel.showcaseItems.value == null) return
                        viewModel.selectedShowcaseItem.value = viewModel.showcaseItems.value!![bind.showcaseFlipper.currentItem]
                    }
                    override fun onLongClick(position: Int) {}
                })
        }

        override fun getItemCount(): Int {
            return viewModel.showcaseItems.value?.size ?: 0
        }

        override fun onBindViewHolder(holder: ShowCaseItemViewHolder, position: Int) {
            holder.bind(viewModel.showcaseItems.value!![position])
        }
    }
}