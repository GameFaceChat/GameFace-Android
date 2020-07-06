/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private var originalName = ""
    private var originalBio = ""

    private val currentUsername = MutableLiveData<String>()
    private val currentName = MutableLiveData<String>()
    private val currentBio = MutableLiveData<String>()

    val changed = MutableLiveData<Boolean>()

    fun setUsername(username: String) {
        currentUsername.value = username
    }

    fun setName(name: String) {
        currentName.value = name
    }

    fun setBio(bio: String) {
        currentBio.value = bio
    }

    fun setOriginalName(name: String) {
        originalName = name
    }

    fun setOriginalBio(bio: String) {
        originalBio = bio
    }

    private fun getName() = currentName.value ?: ""

    private fun getBio() = currentBio.value ?: ""

    fun save() {
        viewModelScope.launch {
            FirebaseHelper.updateUserProfile(mutableMapOf(Profile::name.name to getName(), Profile::bio.name to getBio()))
        }
    }
}