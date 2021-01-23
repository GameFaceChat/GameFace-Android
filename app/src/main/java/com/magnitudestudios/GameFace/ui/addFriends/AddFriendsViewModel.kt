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

package com.magnitudestudios.GameFace.ui.addFriends

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.pojo.UserInfo.Friend
import com.magnitudestudios.GameFace.pojo.UserInfo.FriendRequest
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class AddFriendsViewModel : ViewModel() {
    private val queryString: MutableLiveData<String> = MutableLiveData()

    val results = Transformations.switchMap(queryString) {str ->
        liveData(Dispatchers.IO) {
            emit(UserRepository.getProfilesByUsername(str.toLowerCase(Locale.ROOT)))
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
        if (profile.uid != Firebase.auth.currentUser!!.uid) UserRepository.sendFriendRequest(profile)
    }

    fun deleteFriendRequest(profile: Profile) {
        Log.e("HERE", "FIRST")
        if (profile.uid in getFriendRequestedUIDs()) {
            Log.e("HERE", "SECOND")
            viewModelScope.launch(Dispatchers.IO) {
                UserRepository.deleteFriendRequest(profile.uid, false)
            }
        }
    }

    fun getFriendRequestedUIDs() : List<String> {
        return friendRequestedUIDs.value ?: listOf()
    }
    fun getFriendUIDs() : List<String> {
        return friendUIDs.value ?: listOf()
    }


}