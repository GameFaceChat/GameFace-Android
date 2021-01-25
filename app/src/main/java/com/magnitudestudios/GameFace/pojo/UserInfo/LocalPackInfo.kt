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

package com.magnitudestudios.GameFace.pojo.UserInfo

import com.magnitudestudios.GameFace.pojo.Shop.RemotePackInfo

/**
 * Local pack info
 *
 * @property version        The version of the locally stored pack
 * @property imgPath        The local path to the image
 * @property contentAPath   The file path to the content A of the pack
 * @property contentBPath   The file path to the content B of the pack
 * @property name           The name of the pack
 * @constructor
 *
 * @param id                The ID of the pack
 * @param type              The type of the pack
 */
class LocalPackInfo(
        id : String = "",
        type : String = "",
        val version : Int = 0,
        val imgPath : String = "",
        val contentAPath : String = "",
        val contentBPath : String = "",
        val name : String = ""
) : RemotePackInfo(id, type)