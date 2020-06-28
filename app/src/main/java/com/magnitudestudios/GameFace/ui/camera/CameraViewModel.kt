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
import com.magnitudestudios.GameFace.R
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.network.HTTPRequest
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.VideoCall.SendCall
import com.magnitudestudios.GameFace.repository.SessionHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CameraViewModel : ViewModel() {
    val connectedRoom = MutableLiveData<String>()

    fun setConnectedRoom(name: String) {
        connectedRoom.postValue(name)
    }

    fun createRoom(callback: RoomCallback, url: String, profile: Profile, vararg calls : String ) {
        viewModelScope.launch {
            val roomID = SessionHelper.createRoom(callback, Firebase.auth.uid!!)
            connectedRoom.postValue(roomID)
            calls.forEach {call ->
                HTTPRequest.callUser(url, SendCall(profile, call, roomID))
            }
        }
    }

    fun joinRoom(roomID: String, callback: RoomCallback) {
        viewModelScope.launch {
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