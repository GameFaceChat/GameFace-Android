/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop.tabs

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.SharedItemClicked
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.CardPackBinding
import com.magnitudestudios.GameFace.databinding.FragmentMarketItemsBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.ui.BottomContainerFragmentDirections
import com.magnitudestudios.GameFace.ui.shop.ShopViewModel
import com.magnitudestudios.GameFace.views.CardPackViewHolder

class MarketFragment : Fragment() {
    lateinit var bind : FragmentMarketItemsBinding
    val viewModel: ShopViewModel by navGraphViewModels(R.id.bottom_nav_graph)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentMarketItemsBinding.inflate(inflater, container, false)
//        viewModel = ViewModelProvider(parentFragment as ShopFragment)[ShopViewModel::class.java]
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bind.charadesItems.adapter = ShopRowAdapter()
        bind.tOrDItems.adapter = ShopRowAdapter()
        bind.wouldYouRatherItems.adapter = ShopRowAdapter()
        observeCharades()
    }

    private fun observeCharades() {
        viewModel.charadesItems.observe(viewLifecycleOwner, Observer {
            (bind.charadesItems.adapter as ShopRowAdapter).replaceAll(it ?: listOf())
        })
        viewModel.tOrDItems.observe(viewLifecycleOwner, Observer {
            (bind.tOrDItems.adapter as ShopRowAdapter).replaceAll(it ?: listOf())
        })
        viewModel.wouldYouRatherItems.observe(viewLifecycleOwner, Observer {
            (bind.wouldYouRatherItems.adapter as ShopRowAdapter).replaceAll(it ?: listOf())
        })
    }

    inner class ShopRowAdapter() : SortedRVAdapter<ShopItem>(ShopItem::class.java) {
        override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val holderBinding = CardPackBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
            return CardPackViewHolder(holderBinding, object : SharedItemClicked {

                override fun onClick(position: Int, view: View) {
                    Log.e("CLICKED", position.toString())
                    val clickedItem = Gson().toJson(getitem(position))
                    val extras = FragmentNavigatorExtras(view to view.transitionName)
                    val action = BottomContainerFragmentDirections.actionBottomContainerFragmentToCardPackDetailsFragment(view.transitionName, clickedItem)
                    activity?.findNavController(R.id.mainNavHost)?.navigate(action, extras)
                }

            })
        }

        override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
            holder as CardPackViewHolder
            holder.bind(this.getitem(position))
        }

        override fun areItemsSame(item1: ShopItem, item2: ShopItem): Boolean {
            return item1 == item2
        }

        override fun compareItems(item1: ShopItem, item2: ShopItem): Int {
            if (item1.order != item2.order) return item1.order - item2.order
            if (item1.installs != item1.installs) return item1.installs - item2.installs
            return item1.name.compareTo(item2.name)
        }
    }
}