class RecipeRepository(private val recipeRetrofitService: RecipeRetrofitService) {
    // 레시피 api
    suspend fun getRecipeInfo(name: String) = recipeRetrofitService.getRecipeInfo(name)
}