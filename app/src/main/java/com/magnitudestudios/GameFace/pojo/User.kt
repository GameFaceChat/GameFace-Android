package com.magnitudestudios.GameFace.pojo

import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName

data class User(
        @JvmField
        @NonNull
        @field:SerializedName("uid")
        val uid: String,

        @JvmField
        @field:SerializedName("email")
        val email: String?,

        @JvmField
        @field:SerializedName("username")
        val username: String?,

        @JvmField
        @field:SerializedName("avatar_url")
        val avatarUrl: String?,

        @JvmField
        @field:SerializedName("full_name")
        val fullName: String?

)