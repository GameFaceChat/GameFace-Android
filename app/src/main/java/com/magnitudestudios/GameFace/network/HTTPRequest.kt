/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.network

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.features.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.header
import kotlinx.coroutines.tasks.await


object HTTPRequest {
    suspend fun getServers(url:String) : Resource<String?> {
        val idToken = Firebase.auth.currentUser?.getIdToken(true)?.await()?.token ?: return Resource.error("Invalid Token", null)
        val client = HttpClient(Android)
        return try {
            val s = client.get<String>(url) {
                header("authorization", "Bearer $idToken")
            }
            Resource.success(s)
        } catch (e : ClientRequestException) {
            Log.e("HTTPRequest", "Could not get servers", e.cause)
            Resource.error("Error: ${e.response.status.value}: ${e.response.status.description}", null)
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