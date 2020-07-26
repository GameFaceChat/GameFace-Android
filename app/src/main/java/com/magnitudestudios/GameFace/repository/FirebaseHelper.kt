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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

object FirebaseHelper {
    suspend fun exists(vararg path: String): Boolean {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine { cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("ERROR AT EXISTS", p0.message)
                    cont.resume(false)
                }

                override fun onDataChange(data: DataSnapshot) = cont.resume(data.exists())

            })
        }
    }

    suspend fun getValue(vararg path: String): Any? {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine { cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    cont.cancel(p0.toException())
                }

                override fun onDataChange(data: DataSnapshot) = cont.resume(data.value)
            })
        }
    }
}