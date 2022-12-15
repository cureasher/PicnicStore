package com.oh.app.common.kakao.repository

import com.oh.app.BuildConfig
import com.oh.app.common.KAKAO_POI_ADDRESS
import com.oh.app.data.kakao.local.CurrentLocation
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface KakaoLocalRetrofitService {
    @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
    @GET("v2/local/geo/coord2address.json") // local 좌표 정보 api
    suspend fun getCurrentLocation(
        @Query("x") x: Double, // 경도
        @Query("y") y: Double // 위도
    ): Response<CurrentLocation>

    companion object {
        var retrofitService: KakaoLocalRetrofitService? = null
        fun getInstance() : KakaoLocalRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(KAKAO_POI_ADDRESS)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                retrofitService = retrofit.create(KakaoLocalRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}