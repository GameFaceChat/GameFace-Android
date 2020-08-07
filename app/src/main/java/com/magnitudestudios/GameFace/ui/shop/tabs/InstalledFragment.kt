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
import androidx.core.net.toUri
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.callbacks.SharedItemClicked
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.CardPackBinding
import com.magnitudestudios.GameFace.databinding.FragmentInstalledPacksBinding
import com.magnitudestudios.GameFace.databinding.InstalledPackLayoutBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import com.magnitudestudios.GameFace.repository.ShopRepository
import com.magnitudestudios.GameFace.ui.BottomContainerFragmentDirections
import com.magnitudestudios.GameFace.views.CardPackViewHolder
import com.magnitudestudios.GameFace.views.InstalledCardPackViewHolder
import java.io.File

class InstalledFragment : BaseFragment() {
    private lateinit var bind: FragmentInstalledPacksBinding
    private val mAdapter = InstalledAdapter()
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentInstalledPacksBinding.inflate(inflater, container, false)
        return bind.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val localPacks = ShopRepository.getLocalPacks(requireContext())
        mAdapter.addAll(localPacks)
        bind.installedPacks.apply {
            adapter = mAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    inner class InstalledAdapter() : SortedRVAdapter<LocalPackInfo>(LocalPackInfo::class.java) {
        override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
            val holderBinding = InstalledPackLayoutBinding.inflate(LayoutInflater.from(requireContext()), parent, false)
            return InstalledCardPackViewHolder(holderBinding, object : SharedItemClicked {

                override fun onClick(position: Int, view: View) {
                    Log.e("CLICKED", position.toString())
                    val clickedItem = Gson().toJson(getitem(position))
                    val extras = FragmentNavigatorExtras(view to view.transitionName)
                    val action = BottomContainerFragmentDirections.actionBottomContainerFragmentToCardPackDetailsFragment(view.transitionName, clickedItem)
                    try {
                        activity?.findNavController(R.id.mainNavHost)?.navigate(action, extras)
                    } catch (e: Exception){}
                }

            })
        }

        override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
            holder as InstalledCardPackViewHolder
            holder.bind(this.getitem(position))
        }

        override fun areItemsSame(item1: LocalPackInfo, item2: LocalPackInfo): Boolean {
            return item1 == item2
        }

        override fun compareItems(item1: LocalPackInfo, item2: LocalPackInfo): Int {
            return item1.id.compareTo(item2.id)
        }
    }
}