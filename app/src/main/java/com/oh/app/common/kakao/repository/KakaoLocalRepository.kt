package com.oh.app.common.kakao.repository

class KakaoLocalRepository(private val kakaoLocalRetrofitService: KakaoLocalRetrofitService) {
    // poi정보 키워드로 받아오기
    suspend fun getCurrentLocation(x: Double, y: Double) = kakaoLocalRetrofitService.getCurrentLocation(x, y)
}