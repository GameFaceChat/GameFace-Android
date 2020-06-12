/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

data class FriendRequest (
        @JvmField
        var friendUID: String = "",

        @JvmField
        var sentDate: Any? = null,

        @JvmField
        var accepted: Boolean = false
) {
}