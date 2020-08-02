/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.callbacks.RVRequestButton
import com.magnitudestudios.GameFace.databinding.ItemShowcaseBinding
import com.magnitudestudios.GameFace.databinding.RowRequestsBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

class ShowCaseItemViewHolder(private val bind: ItemShowcaseBinding) : RecyclerView.ViewHolder(bind.root) {
    fun bind(item: ShowCaseItem) {
        Glide.with(itemView).load(item.image).into(bind.showcaseImage)
    }
}