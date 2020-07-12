/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.VideoCall.Member
import com.magnitudestudios.GameFace.repository.SessionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    private val connectedRoom = MutableLiveData<String>()

    private val members = MutableLiveData<Member>()

    fun setConnectedRoom(name: String) {
        connectedRoom.postValue(name)
    }

    fun createRoom(callback: RoomCallback, vararg calls: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val roomID = SessionHelper.createRoom(callback, Firebase.auth.uid!!)
            connectedRoom.postValue(roomID)
            calls.forEach { user ->
                SessionHelper.addMember(uid = user, roomID = roomID)
            }
        }
    }

    fun joinRoom(roomID: String, callback: RoomCallback) {
        viewModelScope.launch(Dispatchers.Unconfined) {
            connectedRoom.postValue(SessionHelper.joinRoom(roomID, callback, Firebase.auth.uid!!))
        }
    }

//    fun callUser(userProfile: Profile, toUID: String, roomID: String, serverURL : String) {
//        viewModelScope.launch(Dispatchers.IO) {
//            HTTPRequest.callUser(serverURL, SendCall(userProfile, toUID, roomID))
//        }
//    }

    fun isInitiator() : Boolean {
        return SessionHelper.initiator
    }


}