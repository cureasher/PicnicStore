package com.oh.app.rest.service

import com.oh.app.data.recipe.RecipeInfoData
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 레시피 정보 가져오는 Retrofit Service
 */
interface RecipeRetrofitService {
    @GET("{pathUrl}/RCP_NM={recipeName}")
    suspend fun getRecipeInfo(
        @Path("pathUrl", encoded = true)
        pathUrl: String,
        @Path("recipeName")
        recipeName: String
    ): Response<RecipeInfoData>
}