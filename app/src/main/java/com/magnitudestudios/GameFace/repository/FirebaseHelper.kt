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

import android.util.Log
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseException
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume

/**
 * Firebase helper utility functions for performing basic Firebase Realtime functions
 *
 */
object FirebaseHelper {
    /**
     * Checks whether the data specified by the string path exists
     *
     * @param path vararg of the path as a string
     * @return boolean value whether the value exists
     */
    suspend fun exists(vararg path: String): Boolean {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine { cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("ERROR AT EXISTS", p0.message)
                    cont.resume(false)
                }

                override fun onDataChange(data: DataSnapshot) {
                    Log.e("EXISTING", data.value.toString())
                    Log.e("EXISTS", data.exists().toString())
                    cont.resume(data.exists())
                }

            })
        }
    }

    /**
     * Get the data at a certain location of the databse specified by path
     *
     * @param path  vararg string path of the location
     * @return      DataSnapshot of the specified location of the database
     * @see DataSnapshot
     */
    suspend fun getValue(vararg path: String): DataSnapshot? {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return suspendCancellableCoroutine { cont ->
            reference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError) {
                    Log.e("ERROR", p0.message, p0.toException())
                    cont.resume(null)
                }

                override fun onDataChange(data: DataSnapshot) = cont.resume(data)
            })
        }
    }

    /**
     * Pushes a value to a certain location in the database
     *
     * @param value object to be pushed
     * @param path  vararg string path of the database location
     * @return
     */
    suspend fun pushValue(value : Any?, vararg path : String) : String {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return try {
            reference.push().setValue(value).await()
            ""
        } catch (e : DatabaseException) {e.message.toString()}
    }

    /**
     * Sets the value of a certain location in the database
     *
     * @param value new object to be set
     * @param path  vararg string path of the database location
     * @return
     */
    suspend fun setValue(value : Any?, vararg path : String) : String {
        var reference = Firebase.database.reference
        for (s in path) reference = reference.child(s)
        return try {
            reference.setValue(value).await()
            ""
        } catch (e : DatabaseException) {e.message.toString()}
    }

    /**
     * Gets the Firebase ID token of the current signed-in user
     *
     * @return string ID Token
     */
    suspend fun getIDToken() : String? {
        return Firebase.auth.currentUser?.getIdToken(true)?.await()?.token
    }
}