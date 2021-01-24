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
import com.google.firebase.database.ServerValue

/**
 * Friend
 *
 * @property uid            The UID of the friend
 * @property addedDate      The date when the friend was added
 * @property minutesCalled  The amount of minutes the friend has called with the current user
 * @constructor Create empty Friend
 */// Key UID, Data Below
data class Friend (
        @JvmField
        var uid: String = "",

        @JvmField
        var addedDate: Any? = ServerValue.TIMESTAMP,

        @JvmField
        var minutesCalled: Int = 0
) {
    /**
     * Get added date
     *
     * @return
     */
    @Exclude
    fun getAddedDate(): Long? {
        return if (addedDate is Long) {
            addedDate as Long?
        } else {
            null
        }
    }
}