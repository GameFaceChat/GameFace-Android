package com.magnitudestudios.GameFace.pojo.VideoCall

data class EmitMessage(
        val fromUID: String = "",
        val toUID: String = "",
        val type: String = "",
        val data: Any? = null
)