package com.magnitudestudios.GameFace.pojo

class EmitMessage {
    @JvmField
    var userID: String? = null
    @JvmField
    var type: String? = null
    @JvmField
    var data: Any? = null

    constructor() {}
    constructor(user: String?, type: String?, data: Any?) {
        userID = user
        this.type = type
        this.data = data
    }
}