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

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.databinding.MemberInformationBinding
import com.magnitudestudios.GameFace.loadProfile
import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus
import com.magnitudestudios.GameFace.pojo.VideoCall.Member

class MemberViewHolder(val binding: MemberInformationBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(member: Member) {
        Glide.with(itemView).loadProfile(member.profile?.profilePic, binding.profilePic)
        Log.e("USERNAME", member.profile?.username.toString())
        binding.username.text = member.profile?.username ?: ""

        binding.status.text = when(member.memberStatus) {
            MemberStatus.ACCEPTED.name -> {
                "In Call"
            }
            MemberStatus.CALLING.name -> {
                "Connecting..."
            }
            MemberStatus.RECEIVED.name -> {
                "Calling..."
            }
            MemberStatus.UNAVAILABLE.name-> {
                "Unavailable"
            }
            else -> "Unknown"
        }
    }
}