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

package com.magnitudestudios.GameFace.ui.shop.details

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import androidx.work.*
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.magnitudestudios.GameFace.R.*
import com.magnitudestudios.GameFace.bases.BaseFragment
import com.magnitudestudios.GameFace.databinding.FragmentPackDetailsBinding
import com.magnitudestudios.GameFace.network.DownloadSinglePack
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.ui.main.MainViewModel

/**
 * Card pack details fragment
 *
 * @constructor Create empty Card pack details fragment
 */
class CardPackDetailsFragment : BaseFragment() {
    private lateinit var bind: FragmentPackDetailsBinding
    val args: CardPackDetailsFragmentArgs by navArgs()
    lateinit var packItem : ShopItem
    lateinit var mainViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        bind = FragmentPackDetailsBinding.inflate(inflater, container, false)
        mainViewModel = activity?.run { ViewModelProvider(this).get(MainViewModel::class.java) }!!
        packItem = Gson().fromJson(args.packItem, ShopItem::class.java)
        return bind.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val imageTransition = args.imageUri
        if (mainViewModel.user.value?.data?.money == null) {
            Toast.makeText(requireContext(), "User information not found!", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
        }
        bind.purchaseBtn.isEnabled = mainViewModel.user.value?.data?.money!! >= packItem.price

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
        if (packItem.price == 0) {
            bind.price.text = getString(string.free_price)
            bind.purchaseBtn.text = getString(string.download_btn)
        } else {
            bind.price.text = packItem.price.toString()
        }

        bind.purchaseBtn.setOnClickListener {
            purchaseAndDownload()
        }
    }

    private fun purchaseAndDownload() {
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