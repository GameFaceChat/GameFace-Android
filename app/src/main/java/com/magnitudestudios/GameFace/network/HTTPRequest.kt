/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.network

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Shop.ShopItem
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import com.magnitudestudios.GameFace.repository.ShopRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/*
Server:
    User Owned Packs:
    - Game Type
    - Pack Name
    - Purchase date
    Shop Packs
    - Game Name
    ...

Client:
- Game Type
- Pack Name
- Purchase Date
- Version
*/

object HTTPRequest {
    private const val SERVERS_URL_BACKEND = "https://us-central1-gameface-chat.cloudfunctions.net/app/servers"
    private const val IMAGE_EXTENSION = "_img.webp"
    private const val CONTENT_A_EXTENSION = "_contentA.txt"
    private const val CONTENT_B_EXTENSION = "_contentB.txt"

    suspend fun getServers() : Resource<String?> {
        val idToken = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token ?: return Resource.error("Invalid Token", null)
        val client = HttpClient(Android)
        return try {
            val s = client.get<String>(SERVERS_URL_BACKEND) {
                header("authorization", "Bearer $idToken")
            }
            Resource.success(s)
        } catch (e : ClientRequestException) {
            Log.e("HTTPRequest", "Could not get servers", e.cause)
            Resource.error("Error: ${e.response.status.value}: ${e.response.status.description}", null)
        }
    }

    private suspend fun downloadImage(context: Context, url : String, width : Int, height : Int) : Bitmap? {
        return suspendCoroutine {
            Glide.with(context).asBitmap()
                    .load(url)
                    .into(object : CustomTarget<Bitmap>(width, height) {
                        override fun onLoadCleared(placeholder: Drawable?) {it.resume(null)}
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) { it.resume(resource) }

                        override fun onLoadFailed(errorDrawable: Drawable?) {
                            super.onLoadFailed(errorDrawable)
                            Log.e("DOWNLOADED", url)
                            it.resume(null)
                        }

                    })
        }
    }

    private suspend fun getContent(textUrl : String) : Resource<String> {
        Log.e("GETTING", textUrl)
        val client = HttpClient(Android)
        return try {
            val s = client.get<String>(textUrl)
            Resource.success(s)

        } catch (e : ClientRequestException) {
            Log.e("HTTPRequest", "Could not get download", e.cause)
            Resource.error("Error: ${e.response.status.value}: ${e.response.status.description}", null)
        }
    }

    suspend fun downloadPack(context: Context, shopItem: ShopItem) : Resource<LocalPackInfo?> {
        try {
            //Get packs folder
            val packsFolder = File(context.filesDir, "packs")
            if (!packsFolder.exists()) {
                Log.e("CREATING DIRECTORY", "PACKS")
                if (packsFolder.mkdir()) Log.e("CREATED PACKS", "YAY")
                else Log.e("PACKS Not created", "BOO")
            } else {
                Log.e("EXISTS PACKS", "YAY")
            }

            //Create new pack folder if it does not exist
            val newPackFolder = File(packsFolder, shopItem.id)
            if (!newPackFolder.exists()) {
                Log.e("CREATING DIRECTORY", shopItem.id)
                if (newPackFolder.mkdir()) Log.e("CREATED ${shopItem.id}", "YAY")
                else Log.e("${shopItem.id} Not created", "BOO")
            } else {
                Log.e("EXISTS ${shopItem.id}", "YAY")
            }

            //Save Image File
            val imageFile = File(newPackFolder, "${shopItem.id}$IMAGE_EXTENSION")
            imageFile.createNewFile()

            //Download Pack Image
            val image = downloadImage(context, shopItem.imgURL, 300, 300)
            if (image != null) {
                image.compress(Bitmap.CompressFormat.WEBP, 100, imageFile.outputStream())
                image.recycle()
            }
            else return Resource.error("Error Fetching Pack Image", null)

            //Get Content A
            val contentAResult = getContent(shopItem.content)
            if (contentAResult.status == Status.ERROR) return Resource.error(contentAResult.message, null)
            val contentAFile = File(newPackFolder, "${shopItem.id}$CONTENT_A_EXTENSION")
            contentAFile.createNewFile()
            contentAFile.writeText(contentAResult.data ?: "")

            //Get Content B if exists (for T or D)
            var contentBPath = ""
            if (shopItem.contentB.isNotEmpty()) {
                val contentBResult = getContent(shopItem.contentB)
                if (contentBResult.status == Status.ERROR) return Resource.error(contentBResult.message, null)
                val contentBFile = File(newPackFolder, "${shopItem.id}$CONTENT_B_EXTENSION")
                contentBFile.createNewFile()
                contentBFile.writeText(contentBResult.data ?: "")
                contentBPath = contentBFile.path
            }
            val localPack = LocalPackInfo(
                    shopItem.id,
                    shopItem.type,
                    shopItem.version_number,
                    imageFile.path,
                    contentAFile.path,
                    contentBPath,
                    shopItem.name
            )
            ShopRepository.addToLocalPacks(context, localPack)
            return Resource.success(localPack)

        } catch (e : IOException) {
            Log.e("IOEXCEPTION: ", e.message, e)
            return Resource.error("Error While Downloading: ${e.message}", null)
        }
        catch (e : Exception) {
            Log.e("EXCEPTION: ", e.message, e)
            return Resource.error("Unknown Error While Downloading: ${e.message}", null)
        }


    }

//    suspend fun callUser(url:String, sendCall: SendCall) {
//        val idToken = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token ?: return
//        val client = HttpClient(Android)
//        try {
//            Log.e("JSON", Gson().toJson(sendCall))
//            client.post<Unit>(url) {
//                header("authorization", "Bearer $idToken")
//                header("Content-Type", "application/json")
//                body = Gson().toJson(sendCall)
//            }
//        }
//        catch (e : ClientRequestException) {
//            Log.e("EXCEPTION POST", e.message, e)
//        }
//    }
}