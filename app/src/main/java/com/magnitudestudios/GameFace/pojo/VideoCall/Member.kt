/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.VideoCall

import androidx.annotation.NonNull
import com.google.firebase.database.ServerValue
import com.magnitudestudios.GameFace.pojo.EnumClasses.MemberStatus

data class Member (
        @NonNull
        val uid: String = "",
        val roomID: String = "",
        val memberStatus: String = MemberStatus.CALLING.name,
        val timestamp: Any = ServerValue.TIMESTAMP
)