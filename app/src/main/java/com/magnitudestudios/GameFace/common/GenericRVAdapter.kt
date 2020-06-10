/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.common

import android.content.Context
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView


abstract class GenericRVAdapter<T>(context: Context, items: MutableList<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val context: Context = context
    private var items: MutableList<T>
    abstract fun setViewHolder(parent: ViewGroup?): RecyclerView.ViewHolder
    abstract fun onBindData(holder: RecyclerView.ViewHolder?, `val`: T)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return setViewHolder(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onBindData(holder, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun addItems(savedCardItems: ArrayList<T>) {
        items = savedCardItems
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T {
        return items[position]
    }

    init {
        this.items = items
    }
}