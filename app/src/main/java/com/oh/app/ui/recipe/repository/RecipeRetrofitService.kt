import com.oh.app.BuildConfig
import com.oh.app.common.LAST_URL
import com.oh.app.common.RECIPE_ADDRESS
import com.oh.app.data.recipe.RecipeInfoData
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface RecipeRetrofitService {
    @GET("{startPage}/{endPage}/RCP_NM={recipeName}")
    suspend fun getRecipeInfo(
        @Path("startPage")
        startPage : Int,
        @Path("endPage")
        endPage : Int,
        @Path("recipeName")
        recipeName: String
    ): Response<RecipeInfoData>

    companion object {
        var retrofitService: RecipeRetrofitService? = null
        fun getInstance(): RecipeRetrofitService {
            if (retrofitService == null) {
                val retrofit = Retrofit.Builder()
                    .baseUrl(RECIPE_ADDRESS + BuildConfig.RCP_API_KEY + LAST_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                retrofitService = retrofit.create(RecipeRetrofitService::class.java)
            }
            return retrofitService!!
        }
    }
}