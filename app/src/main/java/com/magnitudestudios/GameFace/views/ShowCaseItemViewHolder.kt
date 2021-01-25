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

package com.magnitudestudios.GameFace.views

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.magnitudestudios.GameFace.callbacks.RVButtonClick
import com.magnitudestudios.GameFace.databinding.ItemShowcaseBinding
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem

/**
 * Show case item view holder
 *
 * @property bind       The binding of the ShowCaseItem
 * @property listener   The onClick listener
 * @see ItemShowcaseBinding
 * @constructor Create empty Show case item view holder
 */
class ShowCaseItemViewHolder(private val bind: ItemShowcaseBinding, private val listener: RVButtonClick) : RecyclerView.ViewHolder(bind.root) {
    /**
     * Called when a ShowCaseItem is binded to a ViewHolder
     *
     * @param item
     */
    fun bind(item: ShowCaseItem) {
        Glide.with(itemView).load(item.image).transition(DrawableTransitionOptions.withCrossFade()).into(bind.showcaseImage)
        bind.showcaseImage.setOnClickListener {
            listener.onClick(adapterPosition)
        }
    }
}