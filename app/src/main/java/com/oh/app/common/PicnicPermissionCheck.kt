package com.oh.app.common

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

const val RETURN_VALUE = 1004
class PicnicPermissionCheck(private val context: Context, private val owner: Activity) {
    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.ACCESS_NETWORK_STATE
    )
    private val permissionList = mutableListOf<String>()

    fun currentAppCheckPermission(): Boolean {
        for (permission in permissions) {
            val result = ContextCompat.checkSelfPermission(context, permission)
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(permission)
            }
        }
        if (permissions.isNotEmpty()) {
            return false
        }
        return true
    }

    fun currentAppRequestPermissions() {
        ActivityCompat.requestPermissions(owner, permissionList.toTypedArray(), RETURN_VALUE)
    }

    fun currentAppPermissionResult(requestCode: Int, grantResults: IntArray): Boolean {
        if (requestCode == RETURN_VALUE && grantResults.isNotEmpty()) {
            for (result in grantResults) {
                if (result == -1) {
                    return false
                }
            }
        }
        return true
    }
}