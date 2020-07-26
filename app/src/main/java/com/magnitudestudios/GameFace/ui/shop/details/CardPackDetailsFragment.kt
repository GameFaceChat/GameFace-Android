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
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.magnitudestudios.GameFace.databinding.FragmentPackDetailsBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem

class CardPackDetailsFragment : Fragment() {
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
        Log.e("TRANSITION", imageTransition)
        bind.packImage.apply {
            transitionName = imageTransition
            Glide.with(this).load(imageTransition).into(this)
        }
        bind.title.text = packItem.name
        bind.sampleQuestionText.text = packItem.sample_question
        bind.description.text = packItem.description


    }
}