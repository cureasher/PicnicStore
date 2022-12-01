package com.oh.app.ui.picnic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oh.app.data.store.StoreInfo
import com.oh.app.ui.picnic.repository.StoreRepository
import kotlinx.coroutines.*

class StoreViewModel(private var repository: StoreRepository) : ViewModel() {
    val storeList = MutableLiveData<StoreInfo>()
    val errorMessage = MutableLiveData<String>()

    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }

    fun getStoreViewModel() {
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getStoreInfo("용산구") // 레시피 정보

            withContext(Dispatchers.Main) {
                if (infoResponse.isSuccessful) {
                    Log.d("로그", "getStoreViewModel: $infoResponse")
                    Log.d(
                        "로그", "getStoreViewModel: ${
                            infoResponse.body()
                        }"
                    )
                    storeList.value = infoResponse.body()
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