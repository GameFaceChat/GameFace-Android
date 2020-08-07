/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

import com.google.firebase.database.ServerValue

open class RemotePackInfo (
        val id : String = "",
        val type : String = "",
        val purchasedDate : Any? = ServerValue.TIMESTAMP
) {
    fun getPurchasedTime() : Long? {
        return if (purchasedDate is Long) purchasedDate
        else null
    }
}