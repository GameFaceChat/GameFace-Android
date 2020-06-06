package com.magnitudestudios.GameFace.pojo

data class SessionDetails (
        @JvmField
        var username: String? = null,
        @JvmField
        var sessionDescriptionJSON: String? = null
)