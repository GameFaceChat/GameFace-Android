package com.magnitudestudios.GameFace.pojo.VideoCall

import android.util.Log
import com.magnitudestudios.GameFace.pojo.VideoCall.IceServer
import kotlin.collections.ArrayList

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
    fun printAll() {
        Log.d("TAG", "printAll: $accountSid $dateCreated $dateUpdated")
        for (i in iceServers!!) {
            Log.d("TAG", "Ice Servers: " + i.url)
        }
        Log.d("TAG", "printAll: $password $ttl $username")
    }
}