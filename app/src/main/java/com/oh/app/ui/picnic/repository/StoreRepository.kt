package com.oh.app.ui.picnic.repository

class StoreRepository(private val recipeRetrofitService: StoreRetrofitService) {
    // 레시피 api
    suspend fun getStoreInfo(name: String) = recipeRetrofitService.getStoreInfo(name)
}