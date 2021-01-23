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
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.gson.Gson
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Shop.DownloadPackResponse
import com.magnitudestudios.GameFace.pojo.UserInfo.LocalPackInfo
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.repository.ShopRepository
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.ClientRequestException
import io.ktor.client.features.ServerResponseException
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.statement.request
import io.ktor.content.TextContent
import io.ktor.http.ContentType
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

/**
 * Http request utility object that has some convenient functions for use in various
 * places in the application.
 *
 */
object HTTPRequest {
    private const val SERVERS_URL_BACKEND = "https://us-central1-gameface-chat.cloudfunctions.net/app/servers"
    private const val PURCHASE_PACK_BACKEND = "https://us-central1-gameface-chat.cloudfunctions.net/app/purchasePack"
    private const val IMAGE_EXTENSION = "_img.webp"
    private const val CONTENT_A_EXTENSION = "_contentA.txt"
    private const val CONTENT_B_EXTENSION = "_contentB.txt"

    /**
     * Gets the available ICE candidates for the current user by making an API call to the Node JS
     * API. The retrieval of ICE servers is done server-side to protect API keys and secrets
     *
     * @return JSON containing a list of available Server Information
     */
    suspend fun getServers() : Resource<String?> {
        //Get the token to verify that this is a valid user when making the request to the
        //protected endpoint
        val idToken = FirebaseHelper.getIDToken() ?: return Resource.error("Invalid Token", null)
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

    /**
     * Purchases a pack from the store. Makes an API call to the protected endpoint.
     *
     * @param packID    ID of the pack
     * @param packType  The type of the pack (game type)
     * @return          Success or failure depending on whether the purchase was successful
     */
    private suspend fun purchasePack(packID: String, packType: String) : Resource<DownloadPackResponse> {
        val idToken = FirebaseHelper.getIDToken() ?: return Resource.error("Invalid Token", null)
        val client = HttpClient(Android)
        Log.e(packID, packType)
        return try {
            val response = client.post<String>(PURCHASE_PACK_BACKEND) {
                header("authorization", "Bearer $idToken")
                header("contentType", "application/json")
                body = TextContent(Gson().toJson(SealedPackInfoToSend(packID, packType)), contentType = ContentType.Application.Json)
            }
            Resource.success(Gson().fromJson(response, DownloadPackResponse::class.java))
        } catch (e : ClientRequestException) {
            Log.e("HTTPRequest", "Client Error ${e.response.request}" )
            Resource.error("Error: ${e.response.status.value}: ${e.response.status.description}", null)
        } catch (e : ServerResponseException) {
            Log.e("HTTPRequest", "Internal Server Error", e.cause)
            Resource.error("Error: ${e.response.status.value}: ${e.response.status.description}", null)
        }
    }

    private data class SealedPackInfoToSend(val packID : String = "", val packType : String = "")

    /**
     * Downloads an image. This is mostly used for downloading the images for packs, but can be
     * extended to download profile pictures and other assets as well
     *
     * @param context   Context of the caller (activity preferable)
     * @param url       URL of the image
     * @param width     Width of the image  (approximation)
     * @param height    Height of the image (approximation)
     * @return  A bitmap of the image that was downloaded that is ready to be saved to a file
     */
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

    /**
     * Gets text contents of a pack. Simple download request
     *
     * @param textUrl
     * @return  Success contains the string contents of the file.
     */
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

    /**
     * Download pack from the server. Downloads the image file, the text content and creates
     * a local folder and stores the results from the download in it. The user should now have these
     * packs online or offline.
     *
     * @param context   Context (preferably applicationContext)
     * @param packID    ID of the pack to be downloaded to the device
     * @param packType  The type of the pack to be downloaded
     * @return          A data class (LocalPackInfo) if successful
     *
     * @see LocalPackInfo
     */
    suspend fun downloadPack(context: Context, packID: String, packType: String) : Resource<LocalPackInfo?> {
        try {
            val responseResult = purchasePack(packID, packType)
            if (responseResult.status == Status.ERROR) return Resource.error(responseResult.message, null)
            val response = responseResult.data!!
            with(response) {
                Log.e("RESPONSE", "$packID $packType $content $contentB $imgURL ${this.version_number}")
            }
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
            val newPackFolder = File(packsFolder, response.packID)
            if (!newPackFolder.exists()) {
                Log.e("CREATING DIRECTORY", response.packID)
                if (newPackFolder.mkdir()) Log.e("CREATED ${response.packID}", "YAY")
                else Log.e("${response.packID} Not created", "BOO")
            } else {
                Log.e("EXISTS ${response.packID}", "YAY")
            }

            //Save Image File
            val imageFile = File(newPackFolder, "${response.packID}$IMAGE_EXTENSION")
            imageFile.createNewFile()

            //Download Pack Image
            val image = downloadImage(context, response.imgURL, 300, 300)
            if (image != null) {
                image.compress(Bitmap.CompressFormat.WEBP, 100, imageFile.outputStream())
                image.recycle()
            }
            else return Resource.error("Error Fetching Pack Image", null)

            //Get Content A
            val contentAResult = getContent(response.content)
            if (contentAResult.status == Status.ERROR) return Resource.error(contentAResult.message, null)
            val contentAFile = File(newPackFolder, "${response.packID}$CONTENT_A_EXTENSION")
            contentAFile.createNewFile()
            contentAFile.writeText(contentAResult.data ?: "")

            //Get Content B if exists (for T or D)
            var contentBPath = ""
            if (response.contentB.isNotEmpty()) {
                val contentBResult = getContent(response.contentB)
                if (contentBResult.status == Status.ERROR) return Resource.error(contentBResult.message, null)
                val contentBFile = File(newPackFolder, "${response.packID}$CONTENT_B_EXTENSION")
                contentBFile.createNewFile()
                contentBFile.writeText(contentBResult.data ?: "")
                contentBPath = contentBFile.path
            }
            val localPack = LocalPackInfo(
                    response.packID,
                    response.packType,
                    response.version_number,
                    imageFile.path,
                    contentAFile.path,
                    contentBPath,
                    response.name
            )
            //Add the list of local packs
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