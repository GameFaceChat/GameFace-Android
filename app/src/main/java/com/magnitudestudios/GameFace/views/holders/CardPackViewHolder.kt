/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views.holders

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.SharedItemClicked
import com.magnitudestudios.GameFace.databinding.CardPackBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import java.io.File

class CardPackViewHolder(val binding: CardPackBinding, val listener: SharedItemClicked) : RecyclerView.ViewHolder(binding.root) {
    fun bind(item: ShopItem) {
        populate(item.imgURL, item.name)
        setListeners()
    }
    private fun setListeners() {
        itemView.setOnTouchListener { v, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    v.alpha = 0.5f
                    v.scaleX = 0.9f
                    v.scaleY = 0.9f
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_CANCEL -> {
                    v.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    return@setOnTouchListener false
                }
                MotionEvent.ACTION_UP -> {
                    v.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
                    v.performClick()
                    return@setOnTouchListener false
                }
//                else -> v.animate().alpha(1.0f).scaleX(1.0f).scaleY(1.0f).setDuration(200).start()
            }
            return@setOnTouchListener true
        }
        itemView.setOnClickListener {
            listener.onClick(adapterPosition, binding.packImage)
        }
    }

    fun populate(imgURL : String, title : String) {
        Glide.with(itemView)
                .load(imgURL)
                .placeholder(R.drawable.shop_item_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.shop_item_placeholder)
                .into(binding.packImage)

        binding.packName.text = title
        binding.packImage.transitionName = imgURL
    }


}