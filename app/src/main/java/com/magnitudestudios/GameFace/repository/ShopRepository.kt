/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.DiffUtil
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.google.gson.JsonParseException
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.genericType
import com.magnitudestudios.GameFace.pojo.Shop.Pack
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
    fun getLocalPacks(context : Context) : List<Pack> {
        val toDeserialize = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.INSTALLED_PACKS_KEY, "")
        return try {
            Gson().fromJson<List<Pack>>(toDeserialize,genericType<List<Pack>>())
        }
        catch (e: Exception) {
            Log.e("GET_INSTALLED", e.message, e)
            listOf()
        }
    }

    suspend fun fetchRemotePacks() : List<Pack> {
        val all = mutableListOf<Pack?>()
        if (Firebase.auth.currentUser?.uid == null) return listOf()
        FirebaseHelper.getValue(Constants.OWNED_PACKS, Firebase.auth.currentUser!!.uid)?.children?.forEach {
            all.add(it.getValue(Pack::class.java))
        }
        return all.filterNotNull()
    }

    suspend fun verifyPacks(context: Context) : List<Pack> {
        val remotePacks = fetchRemotePacks()
        val localPacks = getLocalPacks(context).toSet()

        val toFetch = mutableListOf<Pack>()
        remotePacks.forEach {
            if (!localPacks.contains(it)) toFetch.add(it)
        }

        return toFetch
    }

    suspend fun downloadAll(toDownload : List<Pack>) {
        toDownload.forEach {
            
        }
    }

}