package com.magnitudestudios.GameFace.pojo

data class IceServer (
    @JvmField
    val url: String,
    @JvmField
    val username: String,
    @JvmField
    val urls: String? = null,
    @JvmField
    val credential: String
)
{
    constructor() : this("", "", "", "")
}