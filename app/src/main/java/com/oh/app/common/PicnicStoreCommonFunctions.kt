package com.oh.app.common

import android.widget.Toast

fun toastMessage(message: String) {
    Toast.makeText(PicnicStoreApplication.getAppInstance(), message, Toast.LENGTH_SHORT).show()
}

// 메인 유알엘만 넣고 포트번호까지만 준다. 패스는 함수에
const val TAG = "TagOh"
const val RECIPE_ADDRESS = "http://openapi.foodsafetykorea.go.kr/api/"
const val LAST_URL = "/COOKRCP01/json/1/1000"

const val STORE_ADDRESS = "http://openapi.seoul.go.kr:8088/"
const val STORE_LAST_URL = "/json/CrtfcUpsoInfo/1/50/ / / / /"

const val MART_ADDRESS = "http://openapi.seoul.go.kr:8088/"
const val MART_LAST_URL = "/json/ListNecessariesPricesService/1/1000"

const val KAKAO_POI_ADDRESS = "https://dapi.kakao.com/"
const val LIGHT_MODE = "light"
const val DARK_MODE = "dark"
const val DEFAULT_MODE = "default"
const val LBS_CHECK_CODE = 100
const val INTERVAL_TIME = 250L

var SELECT_CHIP: String = "김치"