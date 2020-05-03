package com.magnitudestudios.GameFace.Network

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.magnitudestudios.GameFace.Interfaces.ReferenceExists

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
        }, "users", mAuth.uid!!,"username")
    }

    init {
        mAuth = FirebaseAuth.getInstance()
        mDatabase = FirebaseDatabase.getInstance()
        firebaseUser = mAuth.currentUser
        getUsername()
    }
}