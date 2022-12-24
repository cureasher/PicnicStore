package com.oh.app.ui.thema

import android.os.Build
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import com.oh.app.common.DARK_MODE
import com.oh.app.common.DEFAULT_MODE
import com.oh.app.common.LIGHT_MODE
import com.oh.app.common.TAG

/**
 * 테마 설정 유틸
 */
class ThemaUtil {
    fun applyTheme(themeColor: String?) {
        when (themeColor) {
            LIGHT_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            DARK_MODE -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            DEFAULT_MODE ->
                // 안드로이드 10 이상
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                    Log.d(TAG, "applyTheme: 10이상")
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY)
                    Log.d(TAG, "applyTheme : 9이하")
                }
        }
    }
}