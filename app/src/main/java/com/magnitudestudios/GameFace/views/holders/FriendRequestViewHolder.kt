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