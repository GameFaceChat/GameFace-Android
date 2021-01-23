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

package com.magnitudestudios.GameFace.repository

import android.content.Context
import android.util.Log
import androidx.preference.PreferenceManager
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.genericType
import com.magnitudestudios.GameFace.network.HTTPRequest
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Shop.RemotePackInfo
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.pojo.Shop.ShowCaseItem
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object ShopRepository {
    //Gets a shop item
    suspend fun getShopItem(gameType : String, packID : String) : ShopItem? {
        return try {FirebaseHelper.getValue(Constants.STORE_PATH, gameType, packID)?.getValue(ShopItem::class.java)?.apply {
            type = gameType
            id = packID
        }}
        catch (e : Exception) {
            Log.e("LOAD PACK ERROR", e.message, e)
            null
        }
    }

    private suspend fun getItems(path: String): List<ShopItem> {
        Log.e("GETTING", "ITEMS")
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
                            snapshot.children.forEach {
                                item -> items.add(item.getValue(ShopItem::class.java)?.apply {
                                    type = path
                                    id = item.key ?: ""
                                })
                            }
                            it.resume(items.filterNotNull().toList())
                        }

                    })
        }
    }

    suspend fun getCharadesItems(): List<ShopItem> {
        return getItems(Constants.CHARADES_PATH)
    }

    suspend fun getTorDItems() : List<ShopItem> {
        return getItems(Constants.TRUTH_OR_DARE_PATH)
    }

    suspend fun getWouldYouRatherItems() : List<ShopItem> {
        return getItems(Constants.WOULD_YOU_RATHER_PATH)
    }

    suspend fun getNumberOfRemotePacks() : Int {
         return FirebaseHelper.getValue(Constants.OWNED_PACKS, Firebase.auth.currentUser?.uid!!, "number")?.getValue(Integer::class.java)?.toInt() ?: 0
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

    //Gets all Locally Installed Packs
    fun getLocalPacks(context : Context) : List<LocalPackInfo> {
        val toDeserialize = PreferenceManager.getDefaultSharedPreferences(context).getString(Constants.INSTALLED_PACKS_KEY, "")
        return try {
            Log.e("PACK", toDeserialize.toString())
            Gson().fromJson<List<LocalPackInfo>>(toDeserialize,genericType<List<LocalPackInfo>>())
        }
        catch (e: Exception) {
            Log.e("GET_INSTALLED", e.message, e)
            listOf()
        }
    }

    //Gets the remote packs user should own
    private suspend fun fetchRemotePacks() : List<RemotePackInfo> {
        val all = mutableListOf<RemotePackInfo?>()
        if (Firebase.auth.currentUser?.uid == null) return listOf()
        FirebaseHelper.getValue(Constants.OWNED_PACKS, Firebase.auth.currentUser!!.uid, Constants.REMOTE_PACKS_KEY)?.children?.forEach {
            all.add(it.getValue(RemotePackInfo::class.java))
        }
        return all.filterNotNull()
    }

    //Adds to the Local Pack Info
    fun addToLocalPacks(context: Context, pack : LocalPackInfo) {
        val newArr = getLocalPacks(context).toMutableList().apply { add(pack) }
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(Constants.INSTALLED_PACKS_KEY, Gson().toJson(newArr))
                .apply()
    }

    //Returns a list of packs that need downloading from server
    suspend fun checkWithRemotePacks(context: Context) : List<RemotePackInfo> {
        val remotePacks = fetchRemotePacks()
        val localPackNames = getLocalPacks(context).map { it.id }.toSet()

        val toFetch = mutableListOf<RemotePackInfo>()

        remotePacks.forEach {remote ->
            if (!localPackNames.contains(remote.id)) toFetch.add(remote)
        }

        return toFetch
    }

    //Download the user's owned packs from server
    suspend fun downloadAll(context: Context, toDownload : List<RemotePackInfo>) : Resource<List<RemotePackInfo>> {
        val failed = mutableListOf<RemotePackInfo>()
        toDownload.forEach {remotePack ->
            val downloadResult = HTTPRequest.downloadPack(context, remotePack.id, remotePack.type)
            when (downloadResult.status) {
                Status.ERROR -> {
                    Log.e("DOWNLOAD ERROR", downloadResult.message.toString())
                    failed.add(remotePack)
                }
                Status.SUCCESS -> addToLocalPacks(context, downloadResult.data!!)
                else -> Log.e("UNKNOWN", "UNKNOWN DOWNLOAD RESULT")
            }
        }
        return if (failed.isEmpty()) Resource.success(null)
        else Resource.error("An error occurred when downloading some files.", failed)
    }

}