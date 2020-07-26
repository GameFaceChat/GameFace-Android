/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

data class ShopItem constructor(
        val name: String = "GameFacePack",
        val description: String = "",
        val content : String = "",
        val imgURL : String = "",
        val date_released : String = "",
        val price : Int = 0,
        val installs: Int = 0,
        val sample_question: String = ""
) {
    override fun equals(other: Any?): Boolean {
        if (other !is ShopItem) return false
        return this.name == other.name &&
                this.description == other.description &&
                this.content == other.name &&
                this.imgURL == other.imgURL &&
                this.date_released == other.date_released &&
                this.price == other.price &&
                this.installs == other.installs &&
                this.sample_question == other.sample_question
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + description.hashCode()
        result = 31 * result + content.hashCode()
        result = 31 * result + imgURL.hashCode()
        result = 31 * result + date_released.hashCode()
        result = 31 * result + price
        result = 31 * result + installs
        result = 31 * result + sample_question.hashCode()
        return result
    }

}