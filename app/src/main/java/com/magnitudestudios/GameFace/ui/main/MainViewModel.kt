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

package com.magnitudestudios.GameFace.ui.main

import android.util.Log
import androidx.lifecycle.*
import com.google.firebase.FirebaseException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.repository.UserRepository
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.EnumClasses.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

/**
 * Main view model
 *
 * @constructor Create empty Main view model
 */
class MainViewModel : ViewModel() {
    val user: MutableLiveData<Resource<User>> = MutableLiveData()
    val profile: MutableLiveData<Resource<Profile>> = MutableLiveData()

    val friends = Transformations.map(user) {
        if (it.status == Status.SUCCESS && it.data != null) it.data.friends.values.toList()
        else listOf()
    }
    val friendRequests = Transformations.map(user) {
        if (it.status == Status.SUCCESS && it.data != null) it.data.friendRequests.values.toList()
        else listOf()
    }
    val friendRequestsSent = Transformations.map(user) {
        if (it.status == Status.SUCCESS && it.data != null) it.data.friendRequestsSent.values.toList()
        else listOf()
    }
    private var listener: ValueEventListener? = null

    /**
     * Sign out the current user
     *
     */
    fun signOutUser() {
        if (Firebase.auth.currentUser != null) {
            if (listener != null) {
                UserRepository.getCurrentUserRef().removeEventListener(listener!!)
            }
            Firebase.auth.signOut()
            user.postValue(Resource.success(null))
        }
    }


    private fun listenToUser() {

        if (Firebase.auth.currentUser != null) {
            listener = UserRepository.getCurrentUserRef()
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) { user.postValue(Resource(Status.ERROR, null, p0.message)) }
                        override fun onDataChange(data: DataSnapshot) {
                            try {
                                val userModel = data.getValue(User::class.java)
                                user.postValue(Resource.success(userModel))
                            } catch (e: Exception)  {
                                Log.e("MainViewModel","Error when Getting User: ", e)
                                user.postValue(Resource(Status.ERROR, null, "Error When Fetching User"))
                            }
                        }

                    })
        } else {
            user.value = (Resource(Status.SUCCESS, null, "Login Required"))
        }
    }

    /**
     * Check if the device token has been registered
     *
     */
    fun checkDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!user.value?.data?.devicesID?.containsKey(UserRepository.getDeviceToken())!!) {
                UserRepository.updateDeviceToken(UserRepository.getDeviceToken())
            }
        }
    }

    private fun getProfile() {
        viewModelScope.launch {
            if (Firebase.auth.currentUser == null) {
                profile.postValue(Resource(Status.SUCCESS, null, "Please Sign In"))
            }
            try {
                val remoteProfile = UserRepository.getUserProfileByUID(Firebase.auth.currentUser?.uid!!)
                if (remoteProfile == null) profile.postValue(Resource(Status.SUCCESS, null, "Profile Has Not Been Created"))
                else profile.postValue(Resource.success(remoteProfile))
            } catch (e: FirebaseException) {
                Log.e("ERROR GETTING PROFILE", e.message, e)
                profile.postValue(Resource.error(e.localizedMessage, null))
            }
        }
    }

    init {
        listenToUser()
        getProfile()
    }

}