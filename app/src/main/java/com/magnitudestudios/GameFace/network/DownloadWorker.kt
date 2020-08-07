/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Shop.RemotePackInfo
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.repository.ShopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DownloadAllNecessary(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        const val FAILURE_KEY = "FAILURE"
        const val Progress = "Progress"
    }
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setProgress(workDataOf(Progress to 0))
        val packs = ShopRepository.checkWithRemotePacks(applicationContext)
        setProgress(workDataOf(Progress to 25))
        val results = ShopRepository.downloadAll(applicationContext, packs)
        setProgress(workDataOf(Progress to 100))
        if (!results.data.isNullOrEmpty()) {
            return@withContext Result.failure(workDataOf(FAILURE_KEY to Gson().toJson(results)))
        }
        return@withContext Result.success()
    }
}

class DownloadSinglePack(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        const val SHOP_ITEM_KEY = "SHOP_ITEM"
        const val Progress = "Progress"
        const val ERROR = "ERROR"
        const val RESULT = "RESULT"
    }
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setProgress(workDataOf(Progress to 0))
        val item = Gson().fromJson(inputData.getString(SHOP_ITEM_KEY), ShopItem::class.java)
        if (item == null) Log.e("NULL", "ITEM")
        setProgress(workDataOf(Progress to 25))
        val result = HTTPRequest.downloadPack(applicationContext, item.id, item.type)
        setProgress(workDataOf(Progress to 100))
        if (result.status == Status.ERROR) {
            return@withContext Result.failure(workDataOf(ERROR to result.message))
        }
        return@withContext Result.success(workDataOf(RESULT to Gson().toJson(result.data)))
    }

}