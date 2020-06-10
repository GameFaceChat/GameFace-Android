/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.bumptech.glide.Glide
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.RowUsersBinding
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.views.UsersViewHolder

class UsersViewAdapter(rvListener: RVButtonClick) : RecyclerView.Adapter<UsersViewHolder>() {
    private val listener = rvListener
    private val sortedList: SortedList<Profile> = SortedList(Profile::class.java, object : SortedList.Callback<Profile>() {
        override fun areItemsTheSame(item1: Profile?, item2: Profile?): Boolean {
            return item1?.username == item2?.username
        }

        override fun onMoved(fromPosition: Int, toPosition: Int) { notifyItemMoved(fromPosition, toPosition)}

        override fun onChanged(position: Int, count: Int) { notifyItemRangeChanged(position, count) }

        override fun onInserted(position: Int, count: Int) { notifyItemRangeInserted(position, count) }

        override fun onRemoved(position: Int, count: Int) { notifyItemRangeRemoved(position, count) }

        override fun compare(o1: Profile, o2: Profile): Int { return o1.username.compareTo(o2.username) }

        override fun areContentsTheSame(oldItem: Profile?, newItem: Profile?): Boolean { return areItemsTheSame(oldItem, newItem) }
    })

    fun add(model: Profile?) { sortedList.add(model) }

    fun remove(model: Profile?) { sortedList.remove(model) }

    fun addAll(models: List<Profile>) { sortedList.addAll(models) }

    fun getitem(position: Int) : Profile { return sortedList.get(position) }

    fun remove(models: List<Profile?>) {
        sortedList.beginBatchedUpdates()
        for (model in models) sortedList.remove(model)
        sortedList.endBatchedUpdates()
    }

    fun replaceAll(models: List<Profile?>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val model: Profile = sortedList.get(i)
            if (!models.contains(model)) sortedList.remove(model)
        }
        sortedList.addAll(models)
        sortedList.endBatchedUpdates()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return UsersViewHolder(RowUsersBinding.inflate(inflater, parent, false), listener)
    }

    override fun onBindViewHolder(holder: UsersViewHolder, position: Int) {
        val value = sortedList.get(position)
        Glide.with(holder.itemView.context).load("https://randomuser.me/api/portraits/med/men/75.jpg").circleCrop().into(holder.getImageView())
        holder.bind(value)

//        holder.getAddFriendButton().setOnClickListener {
//            Toast.makeText(holder.itemView.context, "Clicked Position: $position", Toast.LENGTH_LONG).show()
//        }
    }

    override fun getItemCount(): Int = sortedList.size()

}