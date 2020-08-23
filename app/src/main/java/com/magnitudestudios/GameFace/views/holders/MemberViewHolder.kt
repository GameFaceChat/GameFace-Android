/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
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