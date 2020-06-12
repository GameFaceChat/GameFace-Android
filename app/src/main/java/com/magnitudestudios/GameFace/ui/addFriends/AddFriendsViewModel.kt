/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.addFriends

import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
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


    fun setQueryString(query: String) {
        queryString.value = query
    }

    fun getQueryString(): String? {
        return queryString.value
    }

    fun sendFriendRequest(profile: Profile) {
        if (profile.uid != Firebase.auth.currentUser!!.uid) FirebaseHelper.sendFriendRequest(profile)
    }


}