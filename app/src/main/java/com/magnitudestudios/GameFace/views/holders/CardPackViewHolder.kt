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