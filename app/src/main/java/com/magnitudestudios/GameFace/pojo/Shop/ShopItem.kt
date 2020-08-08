/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

import com.google.firebase.database.Exclude

data class ShopItem constructor(
        val name: String = "GameFacePack",
        val description: String = "",
//        val content : String = "",
//        val contentB : String = "",
        val imgURL : String = "",
        val date_released : String = "",
        val price : Int = 0,
        val installs: Int = 0,
        val samples: List<String> = listOf(),
        val order : Int = 0,
        val version_number : Int = 0,
        val cards : Int = 0,
        @Exclude
        var type : String = "",
        @Exclude
        var id : String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ShopItem) return false
        return this.name == other.name &&
                this.description == other.description &&
                this.imgURL == other.imgURL &&
                this.date_released == other.date_released &&
                this.price == other.price &&
                this.installs == other.installs &&
                this.samples == other.samples &&
                this.order == other.order &&
                this.version_number == other.version_number
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + imgURL.hashCode()
        result = 31 * result + date_released.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + installs.hashCode()
        result = 31 * result + samples.hashCode()
        result = 31 * result + order.hashCode()
        result = 31 * result + version_number.hashCode()
        return result
    }

}