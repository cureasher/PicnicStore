package com.oh.app.rest.service

import com.oh.app.data.store.StoreInfo
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 식당 정보 가져오는 Retrofit Service
 */
interface StoreRetrofitService {
    @GET("{areaName}")
    suspend fun getStoreInfo(@Path("areaName") areaName: String): Response<StoreInfo>
}