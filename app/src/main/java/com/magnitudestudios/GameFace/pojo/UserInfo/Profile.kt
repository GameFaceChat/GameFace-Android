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

package com.magnitudestudios.GameFace.pojo.UserInfo

import androidx.annotation.NonNull
import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue
import com.google.gson.annotations.SerializedName

/**
 * Profile
 *
 * @property uid                The UID of the profile
 * @property username           The username of the profile
 * @property name               The name of the user profile
 * @property bio                The bio of the user profile
 * @property profilePic         The profile picture URI of the user profile
 * @property score              The score of the user profile
 * @property lastLogin          The date of the last login of the user
 * @constructor Create empty Profile
 */
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
        /**
         * Get last login
         *
         * @return
         */
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