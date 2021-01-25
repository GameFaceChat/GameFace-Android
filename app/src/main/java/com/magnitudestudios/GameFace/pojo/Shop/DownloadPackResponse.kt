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

/**
 * Response of a download request made to the API
 *
 * @property packID             The ID of the pack
 * @property packType           The type of the pack
 * @property content            The content of the pack (text)
 * @property contentB           The second file of content if needed
 * @property imgURL             The image URL for the pack
 * @property version_number     The version number of the pack (used for updating packs)
 * @property name               The name of the pack for users
 * @constructor Create empty Download pack response
 */
data class DownloadPackResponse(
        val packID : String = "",
        val packType : String = "",
        val content : String = "",
        val contentB : String = "",
        val imgURL : String = "",
        val version_number : Int = 0,
        val name : String = ""
)