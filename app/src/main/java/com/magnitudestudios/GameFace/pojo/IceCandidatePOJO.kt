package com.magnitudestudios.GameFace.pojo

data class IceCandidatePOJO (
    @JvmField
    val sdpMid: String = "",
    @JvmField
    val sdpMLineIndex: Int = 0,
    @JvmField
    val sdp: String = "",
    @JvmField
    val serverUrl: String = ""
)
{
    constructor() : this("", 0, "", "")
}