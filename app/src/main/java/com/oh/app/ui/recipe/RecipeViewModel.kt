package com.oh.app.ui.recipe

import RecipeRepository
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oh.app.data.recipe.RecipeInfoData
import kotlinx.coroutines.*

class RecipeViewModel(private var repository: RecipeRepository) : ViewModel() {
    val recipeList = MutableLiveData<RecipeInfoData>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }

    fun getRecipeViewModel() {
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getRecipeInfo("김치") // 레시피 정보

            withContext(Dispatchers.Main) {
                if (infoResponse.isSuccessful) {
                    Log.d("로그", "getRecipeViewModel: $infoResponse")
                    Log.d(
                        "로그", "getRecipeViewModel: ${
                            infoResponse.body()
                        }"
                    )
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