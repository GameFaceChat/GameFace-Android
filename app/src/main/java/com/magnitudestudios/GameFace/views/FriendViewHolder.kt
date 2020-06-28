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
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

class FriendViewHolder(val bind: RowFriendsBinding, listener: RVButtonClick) : RecyclerView.ViewHolder(bind.root) {
    init {
        bind.callButton.setOnClickListener { listener.onClick(adapterPosition) }
        bind.callButton.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }

    fun getImageView(): ImageView {
        return bind.profile.profilePic
    }

    fun bind(data: Profile) {
        bind.profile.username.text = data.username
        bind.profile.fullName.text = data.name
    }

}