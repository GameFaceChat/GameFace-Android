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

package com.magnitudestudios.GameFace.ui.profile.tabs

import androidx.lifecycle.*
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Friends view model
 *
 * @constructor Create empty Friends view model
 */
class FriendsViewModel : ViewModel() {
    val friendProfiles = MutableLiveData<List<Profile>>()
    private val queryString = MutableLiveData<String>()
    val searchResultsFriends = Transformations.switchMap(queryString) { query -> getFilteredFriendProfiles(query) }

    val requestUIDs = MutableLiveData<List<String>>()

    val requestProfiles = Transformations.switchMap(requestUIDs) {
        liveData {
            emit(UserRepository.getUserProfilesByUID(it))
        }
    }

    private fun getFilteredFriendProfiles(query: String): LiveData<List<Profile>> {
        return Transformations.map(friendProfiles) { input: List<Profile>? -> input?.filter { query.isEmpty() || it.username.startsWith(query) || it.name.contains(query) } }
    }

    /**
     * Get the profiles of the specified friends UIDs
     *
     * @param uids  The UIDs of the friends
     */
    fun getFriendProfiles(uids: List<String>) {
        viewModelScope.launch(Dispatchers.IO) {
            friendProfiles.postValue(UserRepository.getUserProfilesByUID(uids))
        }
    }

    /**
     * Set the Friend Request UIDs
     *
     * @param uids      The UIDs of the current Friend Requests
     */
    fun setRequestUIDs(uids: List<String>) {
        requestUIDs.value = uids
    }

    /**
     * Sets the search query for the friend
     *
     * @param query The query string for the username of the friend
     */
    fun setQueryFriend(query: String) {
        queryString.value = query
    }

    /**
     * Accepts the Friend Request of this uid
     *
     * @param uid   UID of the friend whose request to accept
     */
    fun acceptFriendRequest(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.acceptFriendRequest(uid)
        }
    }

    /**
     * Deny friend request
     *
     * @param uid   UID of the friend whose request to deny
     */
    fun denyFriendRequest(uid: String) {
        viewModelScope.launch(Dispatchers.IO) {
            UserRepository.deleteFriendRequest(uid, true)
        }
    }
}