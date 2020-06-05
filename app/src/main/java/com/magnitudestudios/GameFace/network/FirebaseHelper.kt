package com.magnitudestudios.GameFace.network

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.callbacks.ReferenceExists
import com.magnitudestudios.GameFace.pojo.User
import kotlinx.coroutines.tasks.await
import java.lang.Exception

object FirebaseHelper {
    private var mAuth: FirebaseAuth
    private val mDatabase: FirebaseDatabase
    private val firebaseUser: FirebaseUser?

    private const val TAG = "FirebaseHelper"

    var username = ""

    fun signOut() {
        mAuth.signOut()
    }

    private fun referenceExists(exists: ReferenceExists, vararg path: String) {
        var databaseReference = mDatabase.reference
        for (ref in path) databaseReference = databaseReference.child(ref)
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                exists.referenceExists(dataSnapshot.exists(), dataSnapshot)
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })
    }

    fun getUsername() {
        if (mAuth.currentUser == null) return
        referenceExists(ReferenceExists { b, data ->
            if (b) {
                this.username = data.value as String
            }
            else {
                this.username = "RANDOM_USER_"+ (Math.random()*100000).toInt()
            }
        }, Constants.USERS_PATH, mAuth.uid!!,"username")
    }

    suspend fun getUserByUID(uid: String): User? {
        if (firebaseUser == null) return null
        return try {
            val data = Firebase.database.getReference(Constants.USERS_PATH)

            null
        }
        catch (e: Exception) {
            null
        }
    }

    init {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        firebaseUser = mAuth.currentUser
        getUsername()
    }
}