/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList


abstract class SortedRVAdapter<T>(objectType: Class<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val sortedList: SortedList<T> = SortedList(objectType, object : SortedList.Callback<T>() {
        override fun areItemsTheSame(item1: T, item2: T): Boolean { return areItemsSame(item1, item2) }

        override fun onMoved(fromPosition: Int, toPosition: Int) { notifyItemMoved(fromPosition, toPosition)}

        override fun onChanged(position: Int, count: Int) { notifyItemRangeChanged(position, count) }

        override fun onInserted(position: Int, count: Int) { notifyItemInserted(position) }

        override fun onRemoved(position: Int, count: Int) { notifyItemRangeRemoved(position, count)}

        override fun compare(o1: T, o2: T): Int { return compareItems(o1, o2) }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean { return areItemsTheSame(oldItem, newItem) }
    })

    fun add(model: T?) { sortedList.add(model) }

    fun remove(model: T?) { sortedList.remove(model) }

    fun addAll(models: List<T>) { sortedList.addAll(models) }

    fun getitem(position: Int) : T { return sortedList.get(position) }

    fun remove(models: List<T?>) {
        sortedList.beginBatchedUpdates()
        for (model in models) sortedList.remove(model)
        sortedList.endBatchedUpdates()
    }

    fun replaceAll(models: List<T?>) {
        sortedList.beginBatchedUpdates()
        for (i in sortedList.size() - 1 downTo 0) {
            val model: T = sortedList.get(i)
            if (!models.contains(model)) sortedList.remove(model)
        }
        sortedList.addAll(models)
        sortedList.endBatchedUpdates()
        notifyDataSetChanged()
    }

    abstract fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    abstract fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int)

    abstract fun areItemsSame(item1: T, item2: T): Boolean

    abstract fun compareItems(item1: T, item2: T): Int

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onViewBinded(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onViewHolderCreated(parent, viewType)
    }

    override fun getItemCount(): Int = sortedList.size()
}