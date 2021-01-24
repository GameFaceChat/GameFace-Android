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

package com.magnitudestudios.GameFace.pojo.VideoCall

import android.util.Log
import com.magnitudestudios.GameFace.pojo.VideoCall.IceServer
import kotlin.collections.ArrayList

/**
 * Server information
 *
 * @property accountSid
 * @property dateCreated
 * @property dateUpdated
 * @property iceServers
 * @property password
 * @property ttl
 * @property username
 * @constructor Create empty Server information
 */
data class ServerInformation (
        @JvmField
        val accountSid: String? = null,
        @JvmField
        val dateCreated: String? = null,
        @JvmField
        val dateUpdated: String? = null,
        @JvmField
        val iceServers: ArrayList<IceServer>? = null,
        @JvmField
        val password: String? = null,
        @JvmField
        val ttl: Int = 0,
        @JvmField
        val username: String? = null
)
{
    constructor() : this("", "", "", ArrayList(), "", 0, "")

    /**
     * Print all for debugging
     *
     */
    fun printAll() {
        Log.d("TAG", "printAll: $accountSid $dateCreated $dateUpdated")
        for (i in iceServers!!) {
            Log.d("TAG", "Ice Servers: " + i.url)
        }
        Log.d("TAG", "printAll: $password $ttl $username")
    }
}