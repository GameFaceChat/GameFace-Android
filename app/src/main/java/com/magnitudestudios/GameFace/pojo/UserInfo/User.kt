/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import androidx.annotation.NonNull
import com.google.gson.annotations.SerializedName

data class User(
        @JvmField
        @NonNull
        var uid: String = "",

        @JvmField
        @NonNull
        var profile: Profile? = Profile(),

        @JvmField
        @NonNull
        var friendRequests: Map<String, String>? = null
)