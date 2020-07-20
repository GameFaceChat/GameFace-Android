/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.ui.camera.addMembers

import android.util.Log
import androidx.lifecycle.*
import com.magnitudestudios.GameFace.pojo.UserInfo.Friend
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import kotlinx.coroutines.Dispatchers

class AddMembersViewModel : ViewModel() {
    var friends = MutableLiveData<List<Friend>>()
    var addedMembers = MutableLiveData<List<String>>()

    var friendProfiles = Transformations.switchMap(friends) {
        liveData(Dispatchers.IO) {
            Log.e("GETTING PROFILES", "NICE")
            if (!friends.value.isNullOrEmpty()) emit(FirebaseHelper.getUserProfilesByUID(friends.value!!.map { it.uid }))
        }
    }


}