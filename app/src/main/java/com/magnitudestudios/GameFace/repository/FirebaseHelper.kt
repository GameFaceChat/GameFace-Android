/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.math.roundToInt

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun getUserByUID(uid: String): User? {
        if (!exists(Constants.USERS_PATH, uid)) return null
        return suspendCoroutine { cont ->
            Firebase.database.reference.child(Constants.USERS_PATH).child(uid).addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    cont.resume(null)
                }

                override fun onDataChange(data: DataSnapshot) {
                    val user = data.getValue(User::class.java)
                    if (user?.profile?.username.isNullOrEmpty()) user?.profile?.username = "random_user_"+ (Math.random()* 100000).roundToInt().toString()
                    cont.resume(user)
                }
            })
        }
    }

    suspend fun exists(vararg path: String) : Boolean {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine {cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("ERROR AT EXISTS", p0.message)
                    cont.cancel(p0.toException())
                }

                override fun onDataChange(data: DataSnapshot) {
                    cont.resume(data.exists())
                }

            })
        }
    }

    suspend fun getValue(vararg path: String) : Any? {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine {cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("ERROR AT hasValue", p0.message)
                    cont.cancel(p0.toException())
                }

                override fun onDataChange(data: DataSnapshot) {
                    cont.resume(data.value)
                }

            })
        }
    }
}