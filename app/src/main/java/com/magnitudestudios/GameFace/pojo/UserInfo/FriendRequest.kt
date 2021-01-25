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

import com.google.firebase.database.Exclude

/**
 * Friend request
 *
 * @property friendUID  The UID of the friend for the request
 * @property sentDate   The date when the request was sent
 * @property accepted   A boolean whether the request has been accepted
 * @constructor Create empty Friend request
 */
data class FriendRequest (
        @JvmField
        var friendUID: String = "",

//        @JvmField
//        var otherUsername: String = "",         //When sent, it is the username of receiver; otherwise,
                                                //When received, it is the username of sender
        @JvmField
        var sentDate: Any? = null,

        @JvmField
        var accepted: Boolean = false
) {
        /**
         * Get the date that this request was sent as a long
         *
         * @return
         */
        @Exclude
        fun getSentDate(): Long? {
                return if (sentDate is Long) {
                        sentDate as Long?
                } else {
                        null
                }
        }
}