package com.magnitudestudios.GameFace.pojo

import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName

data class User(
        @JvmField
        @NonNull
        @field:SerializedName("uid")
        var uid: String,

        @JvmField
        @field:SerializedName("email")
        var email: String?,

        @JvmField
        @field:SerializedName("username")
        var username: String?,

        @JvmField
        @field:SerializedName("avatar_url")
        var avatarUrl: String?,

        @JvmField
        @field:SerializedName("full_name")
        var fullName: String?

) {
        //For Firebase
        constructor() : this("", "", "", "", "")
}