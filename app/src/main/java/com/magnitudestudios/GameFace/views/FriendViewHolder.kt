/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.databinding.RowFriendsBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

class FriendViewHolder(bind: RowFriendsBinding) : RecyclerView.ViewHolder(bind.root) {
    private val mBinding = bind
    fun getImageView(): ImageView {
        return mBinding.profilePic
    }
    fun bind(data: Profile) {
        mBinding.username.text = data.username
        mBinding.fullName.text = data.name
    }

}