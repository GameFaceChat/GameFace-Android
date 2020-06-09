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
        @NonNull
        var name: String = "",

        @JvmField
        @NonNull
        var bio: String = "",

        @JvmField
        @NonNull
        var profilePic: String = "",

        @JvmField
        @NonNull
        var score: Int = 0,

        @JvmField
        @NonNull
        var lastLogin: Any = ServerValue.TIMESTAMP

//        @JvmField
//        @NonNull
//        @field:SerializedName("updated")
//        var updated: Any = ServerValue.TIMESTAMP
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