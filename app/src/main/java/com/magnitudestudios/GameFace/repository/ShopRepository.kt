/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem
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

    suspend fun getShowcaseItems() : List<ShowCaseItem> {
        return suspendCancellableCoroutine {
            Firebase.database.reference
                    .child(Constants.STORE_PATH)
                    .child(Constants.SHOWCASE_PATH)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(error: DatabaseError) {
                            it.cancel(error.toException())
                        }

                        override fun onDataChange(snapshot: DataSnapshot) {
                            val items = mutableListOf<ShowCaseItem?>()
                            snapshot.children.forEach { item -> items.add(item.getValue(ShowCaseItem::class.java)) }
                            it.resume(items.filterNotNull().toList())
                        }

                    })
        }
    }
    fun getDefaultsItems() {}

    suspend fun loadPack(gameType : String, packName : String) : ShopItem? {
        return try {FirebaseHelper.getValue(Constants.STORE_PATH, gameType, packName)?.getValue(ShopItem::class.java)}
        catch (e : Exception) {
            Log.e("LOAD PACK", e.message, e)
            null
        }
    }
    fun getInstalledPacks() {}
    fun getOwnedPacks() {}

}