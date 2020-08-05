/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.shop.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentPackDetailsBinding
import com.magnitudestudios.GameFace.network.DownloadSinglePack
import com.magnitudestudios.GameFace.network.HTTPRequest
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import kotlinx.coroutines.launch

class CardPackDetailsFragment : BaseFragment() {
    private lateinit var bind: FragmentPackDetailsBinding
    val args: CardPackDetailsFragmentArgs by navArgs()
    lateinit var packItem : ShopItem
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentPackDetailsBinding.inflate(inflater, container, false)
        packItem = Gson().fromJson(args.packItem, ShopItem::class.java)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageTransition = args.imageUri
        bind.packImage.apply {
            transitionName = imageTransition
            Glide.with(this).load(imageTransition).into(this)
        }
        bind.title.text = packItem.name
        bind.sampleQuestionText.text = if (packItem.samples.isNotEmpty()) packItem.samples.first() else {
            bind.sampleQuestion.visibility = View.GONE
            ""
        }
        bind.description.text = packItem.description

        bind.price.text = packItem.price.toString()

        bind.purchaseBtn.setOnClickListener {
            val downloadTask = OneTimeWorkRequestBuilder<DownloadSinglePack>().setInputData(
                    workDataOf(DownloadSinglePack.SHOP_ITEM_KEY to Gson().toJson(packItem))
            ).build()
            val id = downloadTask.id
            WorkManager.getInstance(requireActivity().applicationContext).enqueue(downloadTask)
            WorkManager.getInstance(requireActivity().applicationContext).getWorkInfoByIdLiveData(id).observe(viewLifecycleOwner, Observer {
                when (it.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        Toast.makeText(requireContext(), "${packItem.name} finished downloading", Toast.LENGTH_LONG).show()
                        bind.purchaseBtn.setLoading(false)
                    }
                    WorkInfo.State.RUNNING -> {
                        bind.purchaseBtn.setLoading(true)
                    }
                    WorkInfo.State.FAILED -> {
                        bind.purchaseBtn.setLoading(false)
                        Toast.makeText(requireContext(), "Purchase Failed", Toast.LENGTH_LONG).show()
                        Log.e("Failed Purchase", it.outputData.getString(DownloadSinglePack.ERROR) ?: "")
                    }
                    else -> {
                        bind.purchaseBtn.setLoading(false)
                    }
                }
            })
        }
    }
}