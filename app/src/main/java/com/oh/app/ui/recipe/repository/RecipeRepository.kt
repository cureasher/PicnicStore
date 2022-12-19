package com.oh.app.ui.recipe.repository

import com.oh.app.rest.service.RecipeRetrofitService

class RecipeRepository(private val recipeRetrofitService: RecipeRetrofitService) {
    // 레시피 api
    suspend fun getRecipeInfo(pathUrl: String, name: String) = recipeRetrofitService.getRecipeInfo(pathUrl, name)
}