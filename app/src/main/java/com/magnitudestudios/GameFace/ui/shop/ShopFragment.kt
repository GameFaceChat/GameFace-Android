/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.CardPackBinding
import com.magnitudestudios.GameFace.databinding.FragmentShopBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.views.CardPackViewHolder

class ShopFragment : BaseFragment() {

    lateinit var bind:FragmentShopBinding
    lateinit var viewModel: ShopViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentShopBinding.inflate(inflater)
        viewModel = ViewModelProvider(this)[ShopViewModel::class.java]
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
            return CardPackViewHolder(holderBinding)
        }

        override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
            holder as CardPackViewHolder
            holder.bind(this.getitem(position))
        }

        override fun areItemsSame(item1: ShopItem, item2: ShopItem): Boolean {
            return item1 == item2
        }

        override fun compareItems(item1: ShopItem, item2: ShopItem): Int {
            return item1.name.compareTo(item2.name)
        }
    }
}