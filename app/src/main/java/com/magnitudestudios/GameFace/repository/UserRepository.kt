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

import android.net.Uri
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.UserInfo.Friend
import com.magnitudestudios.GameFace.pojo.UserInfo.FriendRequest
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import com.magnitudestudios.GameFace.repository.FirebaseHelper.exists
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * User repository
 * Handles the logic of all user operations
 * - Creating users
 * - Creating profiles
 * - Updating and managing device tokens
 * - Sending and accepting friend requests
 *
 * @constructor Create empty User repository
 */
object UserRepository {
    private const val TAG = "FirebaseHelper"

    /**
     * Get the DatabaseReference of a certain user
     *
     * @param uid
     * @return
     */
    private fun getUserRef(uid: String): DatabaseReference = Firebase.database.reference.child(Constants.USERS_PATH).child(uid)

    /**
     * Gets the current user database reference
     *
     * @return DatabaseReference
     */
    fun getCurrentUserRef(): DatabaseReference = getUserRef(Firebase.auth.currentUser!!.uid)

    /**
     * Gets the profile DatabaseReference
     * @param uid
     * @return
     */
    private fun getProfileRef(uid: String): DatabaseReference = Firebase.database.reference.child(Constants.PROFILE_PATH).child(uid)

    /**
     * Get current user profile ref
     *
     * @return
     */
    private fun getCurrentUserProfileRef(): DatabaseReference = getProfileRef(Firebase.auth.currentUser!!.uid)

    /**
     * Creates user at the user reference
     *
     * @param user
     * @return a Task that can be listened to
     * @see Task
     */
    fun createUser(user: User): Task<Void> {
        return getCurrentUserRef().setValue(user)
    }

    /**
     * Sets a new value for a user
     *
     * @param user
     */
    suspend fun setUser(user: User) {
        getCurrentUserRef().setValue(user).await()
    }

    /**
     * Create profile for a user
     *
     * @param profile   The profile of the current user
     * @return          A task that can be listened to
     * @see Task
     */
    fun createProfile(profile: Profile): Task<Void> {
        return getCurrentUserProfileRef().setValue(profile)
    }

    /**
     * Updates the user profile
     *
     * @param values key value pairs on attributes to update
     * @return
     */
    suspend fun updateUserProfile(values: MutableMap<String, Any>): Boolean {
        return try {
            getCurrentUserProfileRef().updateChildren(values).await()
            true
        } catch (e: FirebaseException) {
            Log.e("ERROR", "Updating User", e)
            false
        }
    }

    /**
     * Gets the current device token
     *
     * @return the device token
     */
    suspend fun getDeviceToken(): String {
        return FirebaseInstanceId.getInstance().instanceId.await().token
    }

    /**
     * Updates the device token stored in the database. Since a user may have multiple devices, yet
     * each token is unique the keys are the tokens, and the values are a simple boolean.
     *
     * @param token
     */
    suspend fun updateDeviceToken(token: String) {
        if (Firebase.auth.currentUser == null) return
        getCurrentUserRef().child(User::devicesID.name).child(token).setValue(true).await()
    }

//    suspend fun getUserByUID(uid: String): User? {
//        if (Firebase.auth.currentUser == null || !exists(Constants.USERS_PATH, uid)) return null
//        return suspendCoroutine { cont ->
//            getUserRef(uid).addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onCancelled(p0: DatabaseError) = cont.resume(null)
//
//                override fun onDataChange(data: DataSnapshot) {
//                    val user = data.getValue(User::class.java)
//                    cont.resume(user)
//                }
//            })
//        }
//    }

    /**
     * Retrieves a user's profile by their User ID.
     *
     * @param uid   The target user's User ID
     * @return      The profile of the user
     * @see Profile
     */
    suspend fun getUserProfileByUID(uid: String): Profile? {
        Log.e("USER", "PROFILE")
        if (Firebase.auth.currentUser == null) return null
        return suspendCoroutine { cont ->
            Firebase.database.reference
                    .child(Constants.PROFILE_PATH).child(uid)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) = cont.resume(null)

                        override fun onDataChange(data: DataSnapshot) {
                            if (!data.exists()) cont.resume(null)
                            else {
                                val userProfile = data.getValue(Profile::class.java)
                                cont.resume(userProfile)
                            }
                        }
                    })
        }
    }

    /**
     * Retrieves multiple user profiles by uids
     *
     * @param uids  a list of UIDs to retrieve user profiles
     * @return
     */
    suspend fun getUserProfilesByUID(uids: List<String>): List<Profile> {
        val temp = mutableListOf<Profile>()
        for (uid in uids) {
            val profile = getUserProfileByUID(uid)
            if (profile != null) temp.add(profile)
        }
        return temp
    }

    /**
     * Query possible usernames in the database given a query string.
     *
     * @param query The query username (does not have to be the entire username)
     * @return  The search results as list of profiles
     * @see Profile
     */
    suspend fun getProfilesByUsername(query: String): Resource<List<Profile>> {
        return suspendCoroutine { cont ->
            Firebase.database.reference.child(Constants.PROFILE_PATH)
                    .orderByChild(Profile::username.name)
                    .startAt(query)
                    .endAt("${query}\uf8ff")
                    .limitToFirst(25)
                    .addListenerForSingleValueEvent(
                            object : ValueEventListener {
                                override fun onCancelled(p0: DatabaseError) = cont.resume(Resource.error(p0.message, null))

                                override fun onDataChange(data: DataSnapshot) {
                                    val temp = mutableListOf<Profile>()
                                    for (snap in data.children) {
                                        val profile = snap.getValue(Profile::class.java)
                                        if (profile != null) temp.add(profile)
                                    }
                                    cont.resume(Resource.success(temp))
                                }

                            }
                    )
        }
    }

    /**
     * Checks whether a username already exists
     *
     * @param username  The username in question
     * @return          A boolean indicating the existence of the username
     */
    suspend fun usernameExists(username: String): Resource<Boolean> {
        return suspendCoroutine {
            Firebase.database.reference
                    .child(Constants.PROFILE_PATH)
                    .orderByChild(Profile::username.name)
                    .equalTo(username)
                    .addListenerForSingleValueEvent(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) = it.resume(Resource.error(p0.message, false))

                        override fun onDataChange(p0: DataSnapshot) = it.resume(Resource.success(p0.exists()))
                    })
        }
    }

    /**
     * Uploads a profile picture to Cloud Storage.
     *
     * @param image
     * @return  When successful, returns a URI to the image location; returns error message otherwise
     * @see Resource
     */
    suspend fun setProfilePic(image: Uri): Resource<Uri?> {
        if (Firebase.auth.currentUser == null) return Resource.error("User must sign in", null)
        return try {
            val uri = Firebase.storage
                    .reference
                    .child("${Constants.USERS_PATH}/${Firebase.auth.currentUser!!.uid}/${Profile::profilePic.name}")
                    .putFile(image).await().storage.downloadUrl.await()
            try { File(image.path!!).delete()} catch (e: Exception) {Log.e("Set Profile Pic", "Cache delete failed", e)}
            Resource.success(uri)
        } catch (e: FirebaseException) {
            Log.e("FirebaseHelper", e.message, e)
            Resource.error(e.message, null)
        }


    }

    /**
     * Sends a friend request to a specific user
     *
     * @param toUser    The profile of the user to send a friend request to
     */
    fun sendFriendRequest(toUser: Profile) {
        //Send Request to Friend
        getUserRef(toUser.uid)
                .child(User::friendRequests.name).child(Firebase.auth.currentUser!!.uid).setValue(
                        FriendRequest(Firebase.auth.currentUser!!.uid, ServerValue.TIMESTAMP, false)
                )

        //Put in friendRequestsSent
        getCurrentUserRef()
                .child(User::friendRequestsSent.name).child(toUser.uid).setValue(
                        FriendRequest(toUser.uid, ServerValue.TIMESTAMP, false)
                )
    }

    /**
     * Remove a friend request from both the user who sent it, and the user who received the request
     *
     * @param uid               The uid of the friend who sent the request
     * @param gotFriendRequest  True if you received the friend request; False if you sent the friend request.
     */
    suspend fun deleteFriendRequest(uid: String, gotFriendRequest: Boolean) {
        getCurrentUserRef()
                .child(if (gotFriendRequest) User::friendRequests.name else User::friendRequestsSent.name)
                .child(uid)
                .removeValue().await()

        getUserRef(uid)
                .child(if (gotFriendRequest) User::friendRequestsSent.name else User::friendRequests.name)
                .child(Firebase.auth.currentUser!!.uid)
                .removeValue().await()
    }

    /**
     * Accept a friend request
     *
     * @param uid   The uid of the user whose request to accept
     */
    suspend fun acceptFriendRequest(uid: String) {
        getCurrentUserRef()
                .child(User::friends.name)
                .child(uid)
                .setValue(Friend(uid))
        getUserRef(uid)
                .child(User::friends.name)
                .child(Firebase.auth.currentUser!!.uid)
                .setValue(Friend(Firebase.auth.currentUser!!.uid))
        deleteFriendRequest(uid, true)
    }

}