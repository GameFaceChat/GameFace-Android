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

package com.magnitudestudios.GameFace.ui.camera

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.magnitudestudios.GameFace.common.SortedRVAdapter
import com.magnitudestudios.GameFace.databinding.MemberInformationBinding
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.views.holders.MemberViewHolder

/**
 * Member status adapter
 *
 * @property members
 * @constructor Create empty Member status adapter
 */
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