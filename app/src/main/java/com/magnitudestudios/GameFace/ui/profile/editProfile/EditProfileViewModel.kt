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

package com.magnitudestudios.GameFace.ui.profile.editProfile

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.magnitudestudios.GameFace.pojo.Helper.Resource
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.launch

/**
 * Edit profile view model
 *
 * @constructor Create empty Edit profile view model
 */
class EditProfileViewModel : ViewModel() {
    private var originalName = ""
    private var originalBio = ""

    private val currentUsername = MutableLiveData<String>()
    private val currentName = MutableLiveData<String>()
    private val currentBio = MutableLiveData<String>()

    val savingProgress = MutableLiveData<Resource<Boolean>>()

    val changed = MutableLiveData<Boolean>()

    /**
     * Set the new username
     *
     * @param username
     */
    fun setUsername(username: String) {
        currentUsername.value = username
        checkChanged()
    }

    /**
     * Set the new name
     *
     * @param name
     */
    fun setName(name: String) {
        currentName.value = name
        checkChanged()
    }

    /**
     * Set the new bio
     *
     * @param bio
     */
    fun setBio(bio: String) {
        currentBio.value = bio
        checkChanged()
    }

    private fun checkChanged() {
        changed.value = (getName() != originalName || getBio() != originalBio)
    }

    /**
     * Sets the original name value before editing
     *
     * @param name
     */
    fun setOriginalName(name: String) {
        originalName = name
        currentName.value = name
    }

    /**
     * Sets the original bio value before editing
     *
     * @param bio
     */
    fun setOriginalBio(bio: String) {
        originalBio = bio
        currentBio.value = bio
    }

    /**
     * Get the name of the user
     *
     */
    fun getName() = currentName.value ?: ""

    /**
     * Get the bio of the user
     *
     */
    fun getBio() = currentBio.value ?: ""

    /**
     * Save the new user Profile
     *
     */
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