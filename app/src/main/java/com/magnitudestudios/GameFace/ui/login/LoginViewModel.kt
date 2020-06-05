/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LoginViewModel : ViewModel() {
    val signUpUsername = MutableLiveData<String>()
    val signUpPassword = MutableLiveData<String>()

    val loginUsername = MutableLiveData<String>()
    val loginPassword = MutableLiveData<String>()

    val authenticatedByGoogle = MutableLiveData<Boolean>()


}