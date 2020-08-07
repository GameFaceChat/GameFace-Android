/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import android.view.MotionEvent
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.SharedItemClicked
import com.magnitudestudios.GameFace.databinding.InstalledPackLayoutBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import java.io.File

class InstalledCardPackViewHolder(val bind : InstalledPackLayoutBinding, val listener: SharedItemClicked) : RecyclerView.ViewHolder(bind.root) {
    fun bind(item: LocalPackInfo) {
        populate(File(item.imgPath))
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
            listener.onClick(adapterPosition, bind.packImage)
        }
    }

    fun populate(imgFile : File) {
        Glide.with(itemView)
                .load(imgFile)
                .placeholder(R.drawable.shop_item_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade())
                .error(R.drawable.shop_item_placeholder)
                .into(bind.packImage)

        bind.packImage.transitionName = imgFile.path
    }
}