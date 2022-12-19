package com.oh.app.rest.service

import com.oh.app.data.mart.MartData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * MartData 가져오는 RetrofitService
 */
interface MartRetrofitService {
    @GET("{pathUrl}/{martName}")
    suspend fun getMartInfo(
        @Path("pathUrl", encoded = true) pathUrl : String,
        @Path("martName") martName: String): Response<MartData>
}