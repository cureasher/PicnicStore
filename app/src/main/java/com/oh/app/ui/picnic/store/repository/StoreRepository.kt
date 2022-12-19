package com.oh.app.ui.picnic.store.repository

import com.oh.app.rest.service.StoreRetrofitService

class StoreRepository(private val recipeRetrofitService: StoreRetrofitService) {
    // 레시피 api
    suspend fun getStoreInfo(name: String) = recipeRetrofitService.getStoreInfo(name)
}