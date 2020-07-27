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
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    private var originalName = ""
    private var originalBio = ""

    private val currentUsername = MutableLiveData<String>()
    private val currentName = MutableLiveData<String>()
    private val currentBio = MutableLiveData<String>()

    val savingProgress = MutableLiveData<Resource<Boolean>>()

    val changed = MutableLiveData<Boolean>()

    fun setUsername(username: String) {
        currentUsername.value = username
        checkChanged()
    }

    fun setName(name: String) {
        currentName.value = name
        checkChanged()
    }

    fun setBio(bio: String) {
        currentBio.value = bio
        checkChanged()
    }

    private fun checkChanged() {
        changed.value = (getName() != originalName || getBio() != originalBio)
    }

    fun setOriginalName(name: String) {
        originalName = name
        currentName.value = name
    }

    fun setOriginalBio(bio: String) {
        originalBio = bio
        currentBio.value = bio
    }

    fun getName() = currentName.value ?: ""

    fun getBio() = currentBio.value ?: ""

    fun save() {
        savingProgress.value = Resource.loading(false)
        viewModelScope.launch {
            if (UserRepository.updateUserProfile(mutableMapOf(Profile::name.name to getName(), Profile::bio.name to getBio()))) {
                savingProgress.postValue(Resource.success(true))
            }
            else savingProgress.postValue(Resource.error("Could not save profile", false))
        }
    }
}