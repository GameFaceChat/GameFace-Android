/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ShopRepository {

    private suspend fun getItems(path: String): List<ShopItem> {
        return suspendCancellableCoroutine {
            Firebase.database.reference
                    .child(Constants.STORE_PATH)
                    .child(path)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            it.cancel(error.toException())
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val items = mutableListOf<ShopItem?>()
                            snapshot.children.forEach { item -> items.add(item.getValue(ShopItem::class.java)) }
                            it.resume(items.filterNotNull().toList())
                        }

                    })
        }
    }
    suspend fun getCharadesItems(): List<ShopItem> {
        Log.e("GETTING", "CHARADES")
        return getItems(Constants.CHARADES_PATH)
    }

    suspend fun getTorDItems() : List<ShopItem> {
        return getItems(Constants.TRUTH_OR_DARE_PATH)
    }

    suspend fun getWouldYouRatherItems() : List<ShopItem> {
        return getItems(Constants.WOULD_YOU_RATHER_PATH)
    }
    fun getDefaultsItems() {}

    fun loadPack() {}
    fun getInstalledPacks() {}
    fun getOwnedPacks() {}

}