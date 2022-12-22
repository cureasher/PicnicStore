package com.oh.app.ui.recipe.repository

import RecipeRetrofitService

class RecipeRepository(private val recipeRetrofitService: RecipeRetrofitService) {
    // 레시피 api
    suspend fun getRecipeInfo(startPage : Int, endPage: Int, name: String) = recipeRetrofitService.getRecipeInfo(startPage, endPage, name)
}