package com.oh.app.common

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.preference.PreferenceManager

private const val IS_LOCATION = "is_location"

class PicnicPreferenceManager {
    companion object {
        private lateinit var manager: PicnicPreferenceManager
        private lateinit var sp: SharedPreferences
        private lateinit var spEditor: SharedPreferences.Editor

        @SuppressLint("CommitPrefEdits")
        fun getInstance(): PicnicPreferenceManager {
            if (this::manager.isInitialized) {
                return manager
            } else {
                sp = PreferenceManager.getDefaultSharedPreferences(
                    PicnicStoreApplication.applicationContext()
                )
                spEditor = sp.edit()
                manager = PicnicPreferenceManager()
            }
            return manager
        }
    }

    var isPermission: Boolean
        get() = sp.getBoolean(IS_LOCATION, false)
        set(permissionCheck) {
            with(spEditor) {
                putBoolean(IS_LOCATION, permissionCheck).apply()
            }
        }
}