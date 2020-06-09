/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.repository

import android.util.Log
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import kotlinx.coroutines.tasks.await

object UserRepository {
    suspend fun loadUser(uid: String) {
        FirebaseHelper.getUserByUID(uid)
    }

    suspend fun setUser(user: User) : Boolean {
        return try {
            Firebase.database.reference.child(Constants.USERS_PATH).child(Firebase.auth.currentUser?.uid!!).setValue(user).await()
            true
        } catch (e: FirebaseException) {
            Log.e("ERROR AT SET USER", e.message, e)
            false
        }
    }
}