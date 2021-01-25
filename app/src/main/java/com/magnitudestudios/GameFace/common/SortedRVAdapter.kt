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

package com.magnitudestudios.GameFace.common

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList


/**
 * Abstract class for a Sorted RecyclerView Adapter
 * @see SortedList
 * @see RecyclerView.Adapter
 */
abstract class SortedRVAdapter<T>(objectType: Class<T>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Sorted list
     * @see SortedList
     */
    private val sortedList: SortedList<T> = SortedList(objectType, object : SortedList.Callback<T>() {
        override fun areItemsTheSame(item1: T, item2: T): Boolean { return areItemsSame(item1, item2) }

        override fun onMoved(fromPosition: Int, toPosition: Int) { notifyItemMoved(fromPosition, toPosition)}

        override fun onChanged(position: Int, count: Int) { notifyItemRangeChanged(position, count) }

        override fun onInserted(position: Int, count: Int) { notifyItemInserted(position) }

        override fun onRemoved(position: Int, count: Int) { notifyItemRangeRemoved(position, count)}

        override fun compare(o1: T, o2: T): Int { return compareItems(o1, o2) }

        override fun areContentsTheSame(oldItem: T, newItem: T): Boolean { return areItemsTheSame(oldItem, newItem) }
    })

    /**
     * Adds a model to the list
     *
     * @param model
     */
    fun add(model: T?) { sortedList.add(model) }

    /**
     * Removes model from the list
     *
     * @param model
     */
    fun remove(model: T?) { sortedList.remove(model) }

    /**
     * Add all the models given as a parameter
     *
     * @param models the models to add to the list
     */
    fun addAll(models: List<T>) { sortedList.addAll(models) }

    /**
     * Get an item from the sorted list
     *
     * @param position of the model. Must be within the bounds of the list size.
     * @throws IndexOutOfBoundsException
     * @return the model at the position specified
     */
    fun getitem(position: Int) : T { return sortedList.get(position) }

    /**
     * Removes items from the sorted list
     *
     * @param models models of the generic type to remove form the list
     */
    fun remove(models: List<T?>) {
        sortedList.beginBatchedUpdates()
        for (model in models) sortedList.remove(model)
        sortedList.endBatchedUpdates()
    }

    /**
     * Replace all
     * Replaces the entire list of sorted items
     * @param models the new list
     */
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

    /**
     * On bind view holder
     *
     * @param holder   ViewHolder for the RecyclerView item
     * @param position position of the item in the list
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        onViewBinded(holder, position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return onViewHolderCreated(parent, viewType)
    }

    override fun getItemCount(): Int = sortedList.size()


    /*
        |--------------------|
        |   Public Methods   |
        |--------------------|
     */

    /**
     * On view holder created
     *
     * @param parent   Parent ViewGroup of the ViewHolder
     * @param viewType id Representing the view Type
     * @return         ViewHolder
     */
    abstract fun onViewHolderCreated(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder

    /**
     * Called when the ViewHolder has been binded to the RecyclerView
     *
     * @param holder    the RecyclerView ViewHolder
     * @param position  position of the ViewHolder
     */
    abstract fun onViewBinded(holder: RecyclerView.ViewHolder, position: Int)

    /**
     * A function to use to check whether two items have changed/modified.
     * e.g check each member of the items whether they are equal or not
     *
     * @param item1
     * @param item2
     * @return
     */
    abstract fun areItemsSame(item1: T, item2: T): Boolean

    /**
     * Function for the comparator of the sorted list.
     *
     * @param item1
     * @param item2
     * @return integer of -1, 0, 1.
     * @see String.compareTo
     */
    abstract fun compareItems(item1: T, item2: T): Int

}