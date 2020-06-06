package com.magnitudestudios.GameFace.pojo

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