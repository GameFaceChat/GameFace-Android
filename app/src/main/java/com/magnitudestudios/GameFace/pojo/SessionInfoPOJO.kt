package com.magnitudestudios.GameFace.pojo

class SessionInfoPOJO {
    @JvmField
    var type: String? = null
    @JvmField
    var description: String? = null

    constructor() {}
    constructor(type: String?, description: String?) {
        this.type = type
        this.description = description
    }
}