package com.oh.app.ui.recipe

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oh.app.BuildConfig
import com.oh.app.common.LAST_URL
import com.oh.app.common.TAG
import com.oh.app.data.recipe.RecipeInfoData
import com.oh.app.ui.recipe.repository.RecipeRepository
import kotlinx.coroutines.*

class RecipeViewModel(private var repository: RecipeRepository) : ViewModel() {
    val recipeList = MutableLiveData<RecipeInfoData>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }


    fun getRecipeViewModel(recipeName: String) {
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val pathUrl = BuildConfig.RCP_API_KEY + LAST_URL
            val infoResponse = repository.getRecipeInfo(pathUrl, recipeName) // 레시피 정보
            Log.d(TAG, "getRecipeViewModel: $infoResponse ")

            withContext(Dispatchers.Main) {
                if (infoResponse.isSuccessful) {
                    recipeList.value = infoResponse.body()
                    isLoading.value = false
                } else {
                    onError("에러내용 : ${infoResponse.message()}")
                }
            }
        }
    }

    private fun onError(message: String) {
        errorMessage.value = message
        isLoading.value = false
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}