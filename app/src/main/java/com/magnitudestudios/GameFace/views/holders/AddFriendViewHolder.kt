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

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.RowUsersBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import java.lang.IllegalStateException

class AddFriendViewHolder(private val mBinding: RowUsersBinding, private val listener: RVButtonClick) : RecyclerView.ViewHolder(mBinding.root) {
    private var state = Constants.STATE_DEFAULT
    init {
        mBinding.sendRequest.setOnClickListener { listener.onClick(adapterPosition) }
        mBinding.sendRequest.setOnLongClickListener {
            listener.onLongClick(adapterPosition)
            return@setOnLongClickListener true
        }
    }
    fun getImageView(): ImageView {
        return mBinding.profile.profilePic
    }
    fun bind(data: Profile) {
        mBinding.profile.username.text = data.username
        mBinding.profile.fullName.text = data.name
    }

    //0 = default | 1 = request sent | 2 = Friends  | 3 = Own Profile
    fun setState(state: Int = 0) {
        this.state = state
        when (state) {
            Constants.STATE_DEFAULT -> {
                mBinding.sendRequest.text = itemView.context.getText(R.string.users_send_request)
                mBinding.sendRequest.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.color_primary_selector)
            }
            Constants.STATE_REQUESTED -> {
                mBinding.sendRequest.text = itemView.context.getText(R.string.users_request_sent)
                mBinding.sendRequest.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.color_accent_selector)
            }
            Constants.STATE_FRIENDS -> {
                mBinding.sendRequest.text = itemView.context.getText(R.string.users_friend_profile)
                mBinding.sendRequest.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.blue_selector)
            }
            Constants.STATE_OWN_PROFILE -> {
                mBinding.sendRequest.text = itemView.context.getText(R.string.users_own_profile)
                mBinding.sendRequest.backgroundTintList = ContextCompat.getColorStateList(itemView.context, R.color.dark_grey_selector)
            }
            else -> {
                throw IllegalStateException("Unknown Type at AddFriend of: $state")
            }
        }
    }
    fun getState() : Int {return state}


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