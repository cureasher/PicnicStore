package com.oh.app.ui.picnic.mart

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oh.app.BuildConfig
import com.oh.app.common.MART_LAST_URL
import com.oh.app.common.TAG
import com.oh.app.common.toastMessage
import com.oh.app.data.mart.MartData
import com.oh.app.ui.picnic.mart.repository.MartRepository
import kotlinx.coroutines.*

class MartViewModel(private var repository: MartRepository) : ViewModel() {
    val martList = MutableLiveData<MartData>()
    private val errorMessage = MutableLiveData<String>()
    private val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }

    fun getMartViewModel(martName : String) {
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)
            val infoResponse = repository.getMartInfo(BuildConfig.MART_API_KEY + MART_LAST_URL, martName) // 마트 정보
            Log.d(TAG, "getMartViewModel: $infoResponse")
            withContext(Dispatchers.Main) {
                if (infoResponse.isSuccessful) {
                    val martData = infoResponse.body()?.martData
                    if(martData == null){
                        toastMessage("$martName 의 데이터가 없습니다.")
                    } else {
                        martList.postValue(infoResponse.body())
                        isLoading.value = false
                    }
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