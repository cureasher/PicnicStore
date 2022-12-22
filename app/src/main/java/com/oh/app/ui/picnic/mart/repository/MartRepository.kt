package com.oh.app.ui.picnic.mart.repository

import com.oh.app.rest.service.MartRetrofitService

class MartRepository(private val martRetrofitService: MartRetrofitService) {
    // 마트 상품 정보 api
    suspend fun getMartInfo(pathUrl: String, martName: String) =
        martRetrofitService.getMartInfo(pathUrl, martName)
}