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

package com.magnitudestudios.GameFace

import android.widget.ImageButton
import android.widget.ImageView
import androidx.camera.core.AspectRatio
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.reflect.TypeToken
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * Load profile
 *
 * @param url
 * @param target
 */
fun RequestManager.loadProfile(url: String, target: ImageButton) {
    if (url.isEmpty()) {
        this.load(R.drawable.ic_add_profile_pic).into(target)
        return
    }
    this.load(url)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .placeholder(R.drawable.profile_placeholder)
            .error(R.drawable.ic_add_profile_pic)
            .transition(DrawableTransitionOptions.withCrossFade())
            .circleCrop()
            .into(target)
}

/**
 * Load profile
 *
 * @param url
 * @param target
 */
fun RequestManager.loadProfile(url: String?, target: ImageView) {
    if (url.isNullOrEmpty()) {
        this.load(R.drawable.ic_user_placeholder).into(target)
        return
    }
    this.load(url)
            .error(R.drawable.ic_user_placeholder)
            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
            .transition(DrawableTransitionOptions.withCrossFade())
            .placeholder(R.drawable.profile_placeholder)
            .circleCrop()
            .into(target)
}

/**
 * Notify observer
 *
 * @param T
 */
fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

/**
 * Generic type
 *
 * @param T
 */
inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

/**
 * Aspect ratio
 *
 * @param width
 * @param height
 * @return
 */
fun aspectRatio(width: Int, height: Int): Int {
    val previewRatio = max(width, height).toDouble() / min(width, height)
    if (abs(previewRatio - (4.0 / 3.0)) <= abs(previewRatio - (16.0 / 9.0))) {
        return AspectRatio.RATIO_4_3
    }
    return AspectRatio.RATIO_16_9
}

/**
 * Do on child added
 *
 * @param action
 * @receiver
 */
inline fun DatabaseReference.doOnChildAdded(
        crossinline action : (snapshot : DataSnapshot) -> Unit
) = addListener(childAdded = action)

/**
 * Do on child removed
 *
 * @param action
 * @receiver
 */
inline fun DatabaseReference.doOnChildRemoved(
        crossinline action : (snapshot : DataSnapshot) -> Unit
) = addListener(childRemoved = action)

/**
 * Do on error
 *
 * @param action
 * @receiver
 */
inline fun DatabaseReference.doOnError(
        crossinline action : (error : DatabaseError) -> Unit
) = addListener(onCancelled = action)

/**
 * Add listener
 *
 * @param childAdded
 * @param childRemoved
 * @param childChanged
 * @param onCancelled
 * @receiver
 * @receiver
 * @receiver
 * @receiver
 * @return
 */
inline fun DatabaseReference.addListener (
        crossinline childAdded: (
                snapshot: DataSnapshot
        ) -> Unit = { _ -> },
        crossinline childRemoved: (
            snapshot: DataSnapshot
        ) -> Unit = { _ -> },
        crossinline childChanged: (
            snapshot: DataSnapshot,
            previousChildName: String?
        ) -> Unit = { _, _ -> },
        crossinline onCancelled: (
            error: DatabaseError
        ) -> Unit = { _ -> }
): ChildEventListener {
    val childEventListener = object : ChildEventListener {
        override fun onCancelled(error: DatabaseError) = onCancelled.invoke(error)
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) = childChanged.invoke(snapshot, previousChildName)
        override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) = childAdded.invoke(snapshot)
        override fun onChildRemoved(snapshot: DataSnapshot) = childRemoved.invoke(snapshot)

    }
    addChildEventListener(childEventListener)
    return childEventListener
}
