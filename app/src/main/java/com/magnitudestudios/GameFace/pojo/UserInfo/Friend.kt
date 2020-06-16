/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import com.google.firebase.database.Exclude
import com.google.firebase.database.ServerValue

// Key UID, Data Below
data class Friend (
        @JvmField
        var uid: String = "",

        @JvmField
        var addedDate: Any? = ServerValue.TIMESTAMP,

        @JvmField
        var minutesCalled: Int = 0
) {
    @Exclude
    fun getAddedDate(): Long? {
        return if (addedDate is Long) {
            addedDate as Long?
        } else {
            null
        }
    }
}