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

/**
 * User
 *
 * @property uid                        The UID of the User
 * @property created                    The date when the user was created
 * @property devicesID                  The device IDs registered to this user
 * @property friendRequests             The friend requests of this user
 * @property friendRequestsSent         The friend requests sent by this user
 * @property friends                    The friends of the user
 * @property money                      The money that this user has
 * @property rooms                      The rooms that this user is part of
 * @constructor Create empty User
 */
data class User(
        @JvmField
        @NonNull
        var uid: String = "",
        var created: Any? = null,
        var devicesID: HashMap<String, Boolean> = HashMap(),
        var friendRequests: HashMap<String, FriendRequest> = HashMap(),
        var friendRequestsSent: HashMap<String, FriendRequest> = HashMap(),
        var friends: Map<String, Friend> = HashMap(),
        var money : Int = 0,
        var rooms: Map<String, Boolean> = HashMap()
) {
        /**
         * Get created
         *
         * @return
         */
        @Exclude
        fun getCreated(): Long? {
                return if (created is Long) {
                        created as Long
                } else {
                        null
                }
        }
}