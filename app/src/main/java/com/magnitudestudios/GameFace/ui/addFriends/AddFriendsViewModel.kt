/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.addFriends

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
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
        Firebase.database.reference.child(Constants.PROFILE_PATH).orderByChild("username")
    }

    fun getQueryString(): String? {
        return queryString.value
    }

}