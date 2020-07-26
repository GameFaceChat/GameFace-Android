/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

data class ShopItem constructor(
        val name: String = "GameFacePack",
        val content : String = "",
        val imgURL : String = "",
        val date_released : String = "",
        val price : Int = 0
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ShopItem) return false
        return this.name == other.name &&
                this.content == other.name &&
                this.imgURL == other.imgURL &&
                this.date_released == other.date_released &&
                this.price == other.price
    }

    override fun hashCode(): Int {
        var value = 7
        value = 31 * name.hashCode() + 31 * content.hashCode() + 31 * imgURL.hashCode() + 31 * date_released.hashCode()
        return value
    }
}