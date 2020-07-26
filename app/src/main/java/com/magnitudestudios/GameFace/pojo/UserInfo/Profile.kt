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
        var uid: String = "",

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

        override fun equals(other: Any?): Boolean {
                if (this === other) return true
                if (javaClass != other?.javaClass) return false

                other as Profile

                if (uid != other.uid) return false
                if (username != other.username) return false
                if (name != other.name) return false
                if (bio != other.bio) return false
                if (profilePic != other.profilePic) return false
                if (score != other.score) return false
                if (lastLogin != other.lastLogin) return false

                return true
        }

        override fun hashCode(): Int {
                var result = uid.hashCode()
                result = 31 * result + username.hashCode()
                result = 31 * result + name.hashCode()
                result = 31 * result + bio.hashCode()
                result = 31 * result + profilePic.hashCode()
                result = 31 * result + score
                result = 31 * result + (lastLogin?.hashCode() ?: 0)
                return result
        }


}