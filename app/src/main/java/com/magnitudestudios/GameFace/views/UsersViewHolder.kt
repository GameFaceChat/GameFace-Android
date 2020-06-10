/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.views

import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.RowUsersBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

class UsersViewHolder(bind: RowUsersBinding, listener: RVButtonClick) : RecyclerView.ViewHolder(bind.root) {
    private val mBinding = bind
    private val mListener = listener
    init {
        mBinding.sendRequest.setOnClickListener { mListener.onClick(adapterPosition) }
        mBinding.sendRequest.setOnLongClickListener {
            mListener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }
    fun getImageView(): ImageView {
        return mBinding.profilePic
    }
    fun bind(data: Profile) {
        mBinding.username.text = data.username
        mBinding.fullName.text = data.name
    }

//    override fun onClick(v: View?) {
//        if (v == mBinding.sendRequest) {
//            mListener.onClick(adapterPosition)
//            mListener.onClick(adapterPosition)
//        }
//    }
//
//    override fun onLongClick(v: View?): Boolean {
//        if (v == mBinding.sendRequest) {
//            mListener.onLongClick(adapterPosition)
//            mListener.onLongClick(adapterPosition)
//        }
//        return true
//    }
}