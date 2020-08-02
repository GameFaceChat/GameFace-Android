/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.magnitudestudios.GameFace.databinding.ItemShowcaseBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem

class ShowCaseItemViewHolder(private val bind: ItemShowcaseBinding) : RecyclerView.ViewHolder(bind.root) {
    fun bind(item: ShowCaseItem) {
        Glide.with(itemView).load(item.image).transition(DrawableTransitionOptions.withCrossFade()).into(bind.showcaseImage)
    }
}