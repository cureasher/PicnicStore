package com.oh.app.common

import android.widget.Toast

fun toastMessage(message: String) {
    Toast.makeText(PicnicStoreApplication.getAppInstance(), message, Toast.LENGTH_SHORT).show()
}

const val RECIPE_ADDRESS = "http://openapi.foodsafetykorea.go.kr/api/"
const val LAST_URL = "/COOKRCP01/json/"

const val STORE_ADDRESS = "http://openapi.seoul.go.kr:8088/"
const val STORE_LAST_URL = "/json/CrtfcUpsoInfo/1/50/ / / / /"