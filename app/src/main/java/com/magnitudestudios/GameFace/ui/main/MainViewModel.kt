/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
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
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.Constants
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel : ViewModel() {
    val user: MutableLiveData<Resource<User>> = MutableLiveData()
    val profile: LiveData<Resource<Profile>> = liveData {
        if (Firebase.auth.currentUser == null) {
            emit(Resource(Status.SUCCESS, null, "Please Sign In"))
            return@liveData
        }
        try {
            val profile = FirebaseHelper.getUserProfileByUID(Firebase.auth.currentUser?.uid!!)
            if (profile == null) emit(Resource(Status.SUCCESS, null, "Profile Has Not Been Created"))
            else emit(Resource.success(profile))
        } catch (e: FirebaseException) {
            Log.e("ERROR GETTING PROFILE", e.message, e)
            emit(Resource.error(e.localizedMessage, null))
        }
    }

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

    fun signOutUser() {
        if (Firebase.auth.currentUser != null) {
            if (listener != null) {
                FirebaseHelper.getCurrentUserRef().removeEventListener(listener!!)
            }
            Firebase.auth.signOut()
            user.postValue(Resource.success(null))
        }
    }


    private fun listenToUser() {

        if (Firebase.auth.currentUser != null) {
            listener = FirebaseHelper.getCurrentUserRef()
                    .addValueEventListener(object : ValueEventListener {
                        override fun onCancelled(p0: DatabaseError) { user.postValue(Resource(Status.ERROR, null, p0.message)) }
                        override fun onDataChange(data: DataSnapshot) {
                            try {
                                Log.e("MainViewModel","Got User data: "+ data.value.toString())
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

    fun checkDevice() {
        viewModelScope.launch(Dispatchers.IO) {
            if (!user.value?.data?.devicesID?.containsKey(FirebaseHelper.getDeviceToken())!!) {
                FirebaseHelper.updateDeviceToken(FirebaseHelper.getDeviceToken())
            }
        }
    }

    init {
        listenToUser()
    }

}