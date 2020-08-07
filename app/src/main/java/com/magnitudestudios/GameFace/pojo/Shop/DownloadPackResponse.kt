/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

data class DownloadPackResponse(
        val packID : String = "",
        val packType : String = "",
        val content : String = "",
        val contentB : String = "",
        val imgURL : String = "",
        val version_number : Int = 0,
        val name : String = ""
)