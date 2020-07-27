/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import androidx.annotation.NonNull
import com.google.firebase.database.Exclude
import com.magnitudestudios.GameFace.pojo.Shop.Pack
import com.magnitudestudios.GameFace.pojo.VideoCall.Member

data class User(
        @JvmField
        @NonNull
        var uid: String = "",
        var created: Any? = null,
        var devicesID: HashMap<String, Boolean> = HashMap(),
        var friendRequests: HashMap<String, FriendRequest> = HashMap(),
        var friendRequestsSent: HashMap<String, FriendRequest> = HashMap(),
        var friends: Map<String, Friend> = HashMap(),
        var packs : Map<String, Pack> = HashMap()
) {
        @Exclude
        fun getCreated(): Long? {
                return if (created is Long) {
                        created as Long?
                } else {
                        null
                }
        }
}