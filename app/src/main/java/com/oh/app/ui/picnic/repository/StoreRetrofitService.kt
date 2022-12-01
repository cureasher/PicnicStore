package com.oh.app.ui.picnic.repository

import com.oh.app.BuildConfig
import com.oh.app.common.STORE_ADDRESS
import com.oh.app.common.STORE_LAST_URL
import com.oh.app.data.store.StoreInfo
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface StoreRetrofitService {
    @GET("{areaName}")
    suspend fun getStoreInfo(@Path("areaName") areaName: String): Response<StoreInfo>

    companion object {
        var retrofitService: StoreRetrofitService? = null
        fun getInstance(): StoreRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(STORE_ADDRESS + BuildConfig.STORE_API_KEY + STORE_LAST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
//                Log.d("로그", "getInstance: baseurl :${STORE_ADDRESS + BuildConfig.STORE_API_KEY + STORE_LAST_URL} ")
                retrofitService = retrofit.create(StoreRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}