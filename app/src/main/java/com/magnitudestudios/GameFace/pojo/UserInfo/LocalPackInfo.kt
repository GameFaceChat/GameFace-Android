/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.UserInfo

import com.magnitudestudios.GameFace.pojo.Shop.RemotePackInfo

class LocalPackInfo(
        id : String = "",
        type : String = "",
        val version : Int = 0,
        val imgPath : String = "",
        val contentAPath : String = "",
        val contentBPath : String = "",
        val name : String = ""
) : RemotePackInfo(id, type)