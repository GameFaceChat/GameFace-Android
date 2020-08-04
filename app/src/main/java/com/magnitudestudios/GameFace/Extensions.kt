/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace

import android.widget.ImageButton
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.RequestManager
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.ktor.utils.io.core.ExperimentalIoApi
import io.ktor.utils.io.core.withBuffer
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

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

fun RequestManager.loadProfile(url: String, target: ImageView) {
    if (url.isEmpty()) {
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

fun <T> MutableLiveData<T>.notifyObserver() {
    this.value = this.value
}

inline fun <reified T> genericType() = object: TypeToken<T>() {}.type

