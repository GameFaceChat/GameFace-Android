package com.magnitudestudios.GameFace.network

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.User
import kotlinx.coroutines.tasks.await

object FirebaseHelper {
    private const val TAG = "FirebaseHelper"

    fun signOut() {
        FirebaseAuth.getInstance().signOut()
    }

    suspend fun getUserByUID(uid: String): User? {
        val a = try {
            Log.e("HERE", uid)
            val data = Firebase.firestore.collection(Constants.USERS_PATH).document(uid).get().await()
            val user = data.toObject(User::class.java)
            Log.e("USER", user!!.uid)
            user
        }
        catch (e: Exception) {
            Log.e("ERROR", "HERE", e)
            null
        }
        if (a?.username.isNullOrEmpty()) a?.username = "RANDOM_USER_"+ (Math.random()*100000).toInt()
        return a
    }

    fun createUser(user: User) {
        Firebase.firestore.collection(Constants.USERS_PATH).document(user.uid).set(user)
    }
}