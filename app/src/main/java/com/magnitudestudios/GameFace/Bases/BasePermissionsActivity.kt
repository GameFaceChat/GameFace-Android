package com.magnitudestudios.GameFace.Bases

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.magnitudestudios.GameFace.Constants

@SuppressLint("Registered")
open class BasePermissionsActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissions(Constants.ALL_PERMISSIONS)
    }

    // Function to check and request permission.
    fun checkPermissions(requestCode: Int) {
        if (!hasPermissions(this, Constants.PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, Constants.PERMISSIONS, requestCode)
        }
    }

    fun hasAllPermissionsGranted(grantResults: IntArray): Boolean {
        for (grantResult in grantResults) {
            if (grantResult == PackageManager.PERMISSION_DENIED) {
                return false
            }
        }
        return true
    }

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