/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.profile

import androidx.lifecycle.*
import com.magnitudestudios.GameFace.callbacks.RoomCallback
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.ShopRepository
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ProfileViewModel : ViewModel() {
    val numberOfInstalledPacks = liveData {
        emit(ShopRepository.getNumberOfRemotePacks())
    }
}