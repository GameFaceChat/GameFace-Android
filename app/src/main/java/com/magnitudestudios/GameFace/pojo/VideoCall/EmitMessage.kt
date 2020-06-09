package com.magnitudestudios.GameFace.pojo.VideoCall

import com.google.firebase.database.ServerValue

data class EmitMessage(
        @JvmField
        val userID: String,
        @JvmField
        val type: String,
        @JvmField
        val data: Any? = null

) {
    constructor() : this("","", "")
}