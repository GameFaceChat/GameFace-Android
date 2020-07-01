/*
 * Copyright (c) 2020 - Magnitude Studios - All Rights Reserved
 * Unauthorized copying of this file, via any medium is prohibited
 * All software is proprietary and confidential
 *
 */

package com.magnitudestudios.GameFace.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream

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