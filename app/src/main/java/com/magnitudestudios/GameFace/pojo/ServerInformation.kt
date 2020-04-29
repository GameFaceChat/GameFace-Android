package com.magnitudestudios.GameFace.pojo

import android.util.Log
import java.util.*

class ServerInformation {
    var accountSid: String? = null
    var dateCreated: String? = null
    var dateUpdated: String? = null
    @JvmField
    var iceServers: ArrayList<IceServer>? = null
    var password: String? = null
    var ttl = 0
    var username: String? = null
    fun printAll() {
        Log.d("TAG", "printAll: $accountSid $dateCreated $dateUpdated")
        for (i in iceServers!!) {
            Log.d("TAG", "Ice Servers: " + i.url)
        }
        Log.d("TAG", "printAll: $password $ttl $username")
    }
}