/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.VideoCall

import com.magnitudestudios.GameFace.pojo.UserInfo.Profile

data class SendCall (
        val fromProfile: Profile,
        val toUID: String = "",
        val roomID: String
) {
}