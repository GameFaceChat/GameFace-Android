/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import com.google.firebase.database.Exclude

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
        @Exclude
        fun getSentDate(): Long? {
                return if (sentDate is Long) {
                        sentDate as Long?
                } else {
                        null
                }
        }
}