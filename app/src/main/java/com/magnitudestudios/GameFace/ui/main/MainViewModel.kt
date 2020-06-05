/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.magnitudestudios.GameFace.network.FirebaseHelper
import com.magnitudestudios.GameFace.pojo.Resource
import com.magnitudestudios.GameFace.pojo.Status
import com.magnitudestudios.GameFace.pojo.User
import com.magnitudestudios.GameFace.repository.UserRepository

class MainViewModel : ViewModel() {
//    val user: LiveData<Resource<User>> = liveData {
//        emit(Resource(Status.LOADING, null, null))
////        userRepo.loadUser(FirebaseAuth.getInstance().currentUser.uid)
//    }
}