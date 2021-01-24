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

package com.magnitudestudios.GameFace.ui.camera.addMembers

import android.util.Log
import androidx.lifecycle.*
import com.magnitudestudios.GameFace.pojo.UserInfo.Friend
import com.magnitudestudios.GameFace.pojo.UserInfo.Profile
import com.magnitudestudios.GameFace.repository.FirebaseHelper
import com.magnitudestudios.GameFace.repository.UserRepository
import kotlinx.coroutines.Dispatchers

/**
 * Add members view model
 *
 * @constructor Create empty Add members view model
 */
class AddMembersViewModel : ViewModel() {
    var friends = MutableLiveData<List<Friend>>()
    var addedMembers = MutableLiveData<List<String>>()

    var friendProfiles = Transformations.switchMap(friends) {
        liveData(Dispatchers.IO) {
            Log.e("GETTING PROFILES", "NICE")
            if (!friends.value.isNullOrEmpty()) emit(UserRepository.getUserProfilesByUID(friends.value!!.map { it.uid }))
        }
    }


}