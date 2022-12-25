package com.oh.app.common

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.content.pm.ActivityInfo
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import com.oh.app.ui.picnic.PicnicFragment
import net.daum.mf.map.api.MapView

class PicnicStoreApplication : Application() {
    companion object {
        private lateinit var appInstance: PicnicStoreApplication
        lateinit var pref : PreferenceUtil
        fun getAppInstance() = appInstance
    }

    override fun onCreate() {
        super.onCreate()
        pref = PreferenceUtil(applicationContext)
        appInstance = this
        // 데이터 입력 코드
        picnicInit()
        // 화면 세로모드 설정
        settingScreenPortrait()

    }

    private fun picnicInit(){
        // 구별 마트 및 시장정보 데이터
        MartInit.setMartMap()
        // 마트별 위치정보 데이터
        MartInit.setMartPoiList()
        // 구 정보 얻어오는 리스트
        MartInit.getGuList()
    }

    private fun settingScreenPortrait() {
        registerActivityLifecycleCallbacks(object : ActivityLifecycleCallbacks {
            @SuppressLint("SourceLockedOrientationActivity")
            override fun onActivityCreated(activity: Activity, saveInstantBundle: Bundle?) {
                activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            }

            override fun onActivityStarted(activity: Activity) {}
            override fun onActivityResumed(activity: Activity) {}
            override fun onActivityPaused(activity: Activity) {}
            override fun onActivityStopped(activity: Activity) {}
            override fun onActivitySaveInstanceState(activity: Activity, bundle: Bundle) {}
            override fun onActivityDestroyed(activity: Activity) {}
        })
    }
}