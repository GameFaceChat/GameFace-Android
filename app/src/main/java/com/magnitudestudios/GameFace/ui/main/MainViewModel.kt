/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.magnitudestudios.GameFace.network.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.Resource
import com.magnitudestudios.GameFace.pojo.Status
import com.magnitudestudios.GameFace.pojo.User

class MainViewModel : ViewModel() {
    val user: LiveData<Resource<User>> = liveData {
        emit(Resource(Status.LOADING, null, null))
        if (Firebase.auth.currentUser == null) emit(Resource(Status.ERROR, null, "Login Required"))
        else emit(Resource(Status.SUCCESS, FirebaseHelper.getUserByUID(Firebase.auth.currentUser!!.uid), null))
    }

}