/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.addFriends

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.pojo.UserInfo.Friend
import com.magnitudestudios.GameFace.pojo.UserInfo.FriendRequest
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import kotlinx.coroutines.Dispatchers

class AddFriendsViewModel : ViewModel() {
    private val queryString: MutableLiveData<String> = MutableLiveData()

    val results = Transformations.switchMap(queryString) {str ->
        liveData(Dispatchers.IO) {
            emit(FirebaseHelper.getProfilesByUsername(str))
        }
    }

    val friendRequestedUIDs = MutableLiveData<List<String>>()

    val friendUIDs = MutableLiveData<List<String>>()

    fun setFriendRequestsSent(a: List<FriendRequest>) {
        friendRequestedUIDs.value = a.map { it.friendUID }
    }
    fun setFriends(userFriends: List<Friend>) {
        friendUIDs.value = userFriends.map { it.uid }
    }
    fun setQueryString(query: String) {
        queryString.value = query
    }

    fun getQueryString(): String? {
        return queryString.value
    }

    fun sendFriendRequest(profile: Profile) {
        if (profile.uid != Firebase.auth.currentUser!!.uid) FirebaseHelper.sendFriendRequest(profile)
        Log.d("HERE","NICe")
    }

    fun getFriendRequestedUIDs() : List<String> {
        return friendRequestedUIDs.value ?: listOf()
    }
    fun getFriendUIDs() : List<String> {
        return friendUIDs.value ?: listOf()
    }


}