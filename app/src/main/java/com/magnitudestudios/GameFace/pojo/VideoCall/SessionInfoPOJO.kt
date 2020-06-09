package com.magnitudestudios.GameFace.pojo.VideoCall

data class SessionInfoPOJO (
    @JvmField
    var type: String? = "",
    @JvmField
    var description: String? = ""
)
{
    constructor() : this("", "")
}