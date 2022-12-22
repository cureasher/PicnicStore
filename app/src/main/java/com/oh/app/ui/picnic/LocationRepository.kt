package com.oh.app.ui.picnic

import com.oh.app.rest.service.KakaoLocalRetrofitService

class LocationRepository(private val kakaoLocalRetrofitService: KakaoLocalRetrofitService)  {
    // 레시피 api
    suspend fun getCurrentInfo(x: Double, y: Double) = kakaoLocalRetrofitService.getCurrentLocation(x,y)
}