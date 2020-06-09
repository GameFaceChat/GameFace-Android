/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.Helper.Status
import com.magnitudestudios.GameFace.pojo.UserInfo.User
import java.lang.Exception

class MainViewModel : ViewModel() {
    val user: LiveData<Resource<User>> = liveData {
        emit(Resource(Status.LOADING, null, null))
        if (Firebase.auth.currentUser == null) emit(Resource(Status.ERROR, null, "Login Required"))
        else {
            try {
                val user = FirebaseHelper.getUserByUID(Firebase.auth.currentUser!!.uid)
                emit(Resource(Status.SUCCESS, user, ""))
            } catch (e: Exception)  {
                Log.e("MainViewModel","Error when Getting User: ", e)
                emit(Resource(Status.ERROR, null, "User Not Found"))
                //Create a User Here?
            }
        }
    }

    fun signOutUser() {
        FirebaseHelper.signOut()
    }

}