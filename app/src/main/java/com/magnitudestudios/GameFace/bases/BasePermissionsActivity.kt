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

package com.magnitudestudios.GameFace.bases

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.magnitudestudios.GameFace.Constants

/**
 * Base Permission class for all activities that require permissions;
 * Extend this class to ensure that the permissions defined in
 * @see Constants.PERMISSIONS are granted
 */
@SuppressLint("Registered")
open class BasePermissionsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions(Constants.ALL_PERMISSIONS)
    }

    /**
     * @param requestCode integer code for the callback function
     * @see onRequestPermissionsResult
     */
    fun checkPermissions(requestCode: Int) {
        if (!hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, requestCode)
        }
    }

    /**
     * Checks all permissions
     * @return : boolean indicating whether all permissions are granted
     */
    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

    /**
     * Callback for result of permissions request
     * @see super.onRequestPermissionsResult
     */
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d(TAG, "onRequestPermissionsResult: $permissions$grantResults")
        if (requestCode == Constants.ALL_PERMISSIONS && grantResults.size > 0) {
            if (hasAllPermissionsGranted(grantResults)) {
                Toast.makeText(this, "All Permissions Granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "All Permission Not Granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val TAG = "BasePermissionsActivity"

        /**
         * Static function to check whether all permissions have been granted
         * @param context       Context (activity context)
         * @param permissions   Array<String>; array of permissions needed
         */
        fun hasPermissions(context: Context?, permissions: Array<String>): Boolean {
            if (context != null) {
                for (permission in permissions) {
                    if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        return false
                    }
                }
            }
            return true
        }
    }
}