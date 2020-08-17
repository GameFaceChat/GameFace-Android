/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.MemberInformationBinding
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.views.holders.MemberViewHolder

class MemberStatusAdapter(val members: List<Member>) : SortedRVAdapter<Member>(Member::class.java) {

    override fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return MemberViewHolder(MemberInformationBinding.inflate(
                LayoutInflater.from(parent.context), parent, false))
    }

    override fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int) {
        holder as MemberViewHolder
        holder.bind(members[position])
    }

    override fun areItemsSame(item1: Member, item2: Member): Boolean {
        return item1.uid == item2.uid
    }

    override fun compareItems(item1: Member, item2: Member): Int {
        return item1.memberStatus.compareTo(item2.memberStatus)
    }

    init {
        addAll(members)
    }
}