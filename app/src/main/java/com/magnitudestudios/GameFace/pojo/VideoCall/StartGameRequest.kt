/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.VideoCall

data class StartGameRequest(
        val gameID : String = "",
        val version : Int = 0,
        val name : String = "",
        val senderUID : String = ""
)