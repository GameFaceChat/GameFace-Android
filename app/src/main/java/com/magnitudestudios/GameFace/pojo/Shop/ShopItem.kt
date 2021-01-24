/*
 * Copyright (c) 2021 -Srihari Vishnu - All Rights Reserved
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT
 * HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF
 * OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *
 */

package com.magnitudestudios.GameFace.pojo.Shop

import com.google.firebase.database.Exclude

/**
 * Shop item
 *
 * @property name           The name of the Shop Item
 * @property description    The description of the Shop Item
 * @property imgURL         The URL of the image for the pack
 * @property date_released  The date that this Shop Item was released
 * @property price          The price of the Shop Item
 * @property installs       The number of installs that this pack has received
 * @property samples        The number of sample cards that this pack has to show users
 * @property order          The order in which to show the pack (if present)
 * @property version_number The version number of the pack (the latest pack version)
 * @property type           The type of the pack
 * @property id             The ID of the pack
 * @constructor Create empty Shop item
 */
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