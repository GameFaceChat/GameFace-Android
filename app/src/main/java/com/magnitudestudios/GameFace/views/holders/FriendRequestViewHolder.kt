/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views.holders

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.callbacks.RVRequestButton
import com.magnitudestudios.GameFace.databinding.RowRequestsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

class FriendRequestViewHolder(private val bind: RowRequestsBinding, private val listener: RVRequestButton) : RecyclerView.ViewHolder(bind.root) {
    init {
        bind.acceptBtn.setOnClickListener { listener.acceptClicked(adapterPosition) }
        bind.denyBtn.setOnClickListener { listener.denyClicked(adapterPosition) }
    }
    fun bind(request: Profile) {
        bind.profile.username.text = request.username
        bind.profile.fullName.text = request.name
    }
    fun getImageView() : ImageView {
        return bind.profile.profilePic
    }
}