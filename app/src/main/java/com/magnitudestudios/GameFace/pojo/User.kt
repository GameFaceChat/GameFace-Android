package com.magnitudestudios.GameFace.pojo

class User() {
    @JvmField
    var email: String? = null

    @JvmField
    var username: String? = null

    constructor(username: String, email: String) : this() {
        this.username = username
        this.email = email
    }
}