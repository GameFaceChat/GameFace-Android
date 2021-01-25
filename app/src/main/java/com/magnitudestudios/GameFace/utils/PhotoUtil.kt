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

package com.magnitudestudios.GameFace.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.*

/**
 * Compress an image at the given File
 *
 * @param file  The file where the image is currently saved to
 */
fun compressImage(file: File?) {
    if (file == null) throw (FileNotFoundException("File not Found in TakePhotoFragment"))

    val options = BitmapFactory.Options().apply {
        inJustDecodeBounds = true
        inSampleSize = 4
    }
    var inputStream = FileInputStream(file)
    BitmapFactory.decodeStream(inputStream, null, options)
    inputStream.close()

    val SIZE = 100

    var scale = 1
    while (options.outWidth / scale / 2 >= SIZE && options.outHeight / scale / 2 >= SIZE) scale *= 2
    Log.e("SCALE", scale.toString())
    val options2 = BitmapFactory.Options().apply { inSampleSize = scale }
    inputStream = FileInputStream(file)

    val compressed = BitmapFactory.decodeStream(inputStream, null, options2)

    val newDimen = if (compressed!!.width < compressed.height) compressed.width else compressed.height
    val resized: Bitmap? = Bitmap.createBitmap(compressed, 0, 0, newDimen, newDimen)

    inputStream.close()

    file.createNewFile()
    val outputStream = FileOutputStream(file)
    resized?.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
}