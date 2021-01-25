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

package com.magnitudestudios.GameFace.network

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.gson.Gson
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.repository.ShopRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * This worker will download all missing packs from the store
 *
 * @constructor
 *
 * @param context
 * @param params WorkerParamater
 */

class DownloadAllNecessary(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {
    companion object {
        const val FAILURE_KEY = "FAILURE"
        const val Progress = "Progress"
    }
    //Asynchronous
    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        /*
            Check remote packs and compare with the ones currently stored locally
            This is done to make sure that if the user purchases a pack on a different device,
            or if the user is new to the current device (first login) or if a purchased pack was
            not downloaded due to some error
         */
        setProgress(workDataOf(Progress to 0))
        val packs = ShopRepository.checkWithRemotePacks(applicationContext)

        //Downloads all packs from the store
        setProgress(workDataOf(Progress to 25))
        val results = ShopRepository.downloadAll(applicationContext, packs)
        setProgress(workDataOf(Progress to 100))

        //Failed request or download failed
        if (!results.data.isNullOrEmpty()) {
            return@withContext Result.failure(workDataOf(FAILURE_KEY to Gson().toJson(results)))
        }
        return@withContext Result.success()
    }
}

/**
 * Downloads a single game pack from the shop. Pass in the ShopItem data class as a JSON
 * string. This Worker will download the pack from the store.
 *
 * @constructor
 *
 * @param context
 * @param params    Worker parameters
 * @see WorkerParameters
 * @see ShopItem
 */
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
        //There was an error with parsing the JSON string
        if (item == null) {
            Log.e("NULL", "ITEM")
            return@withContext Result.failure(workDataOf(ERROR to "No item was given"))
        }
        setProgress(workDataOf(Progress to 25))

        //Download the pack from the store
        val result = HTTPRequest.downloadPack(applicationContext, item.id, item.type)
        setProgress(workDataOf(Progress to 100))

        //There was an error, so return the status of this download to the user
        if (result.status == Status.ERROR) {
            return@withContext Result.failure(workDataOf(ERROR to result.message))
        }
        return@withContext Result.success(workDataOf(RESULT to Gson().toJson(result.data)))
    }

}