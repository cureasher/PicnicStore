package com.oh.app.rest.service

import com.oh.app.BuildConfig
import com.oh.app.data.kakao.local.CurrentLocation
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

/**
 * 위도, 경도 값을 받아서 현재 위치한 구정보 받아오는 RetrofitService
 */
interface KakaoLocalRetrofitService {
    @Headers("Authorization:KakaoAK ${BuildConfig.KAKAO_REST_API_KEY}")
    @GET("v2/local/geo/coord2address.json") // local 좌표 정보 api
    suspend fun getCurrentLocation(
        @Query("x") x: Double, // 경도
        @Query("y") y: Double // 위도
    ): Response<CurrentLocation>
}