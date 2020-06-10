/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import androidx.annotation.NonNull
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import com.google.gson.annotations.SerializedName

data class Profile(
        @JvmField
        @NonNull
        var username: String = "",

        @JvmField
        var name: String = "",

        @JvmField
        var bio: String = "",

        @JvmField
        var profilePic: String = "",

        @JvmField
        var score: Int = 0,

        @JvmField
        var lastLogin: Any? = null
) {
        @Exclude
        fun getLastLogin(): Long? {
                return if (lastLogin is Long) {
                        lastLogin as Long?
                } else {
                        null
                }
        }

}