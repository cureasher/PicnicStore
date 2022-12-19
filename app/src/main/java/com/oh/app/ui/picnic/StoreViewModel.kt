package com.oh.app.ui.picnic

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oh.app.data.store.StoreInfo
import com.oh.app.ui.picnic.repository.StoreRepository
import kotlinx.coroutines.*

class StoreViewModel(
    private var storeRepository: StoreRepository
) : ViewModel() {
    val storeList = MutableLiveData<StoreInfo>()
    val errorMessage = MutableLiveData<String>()
    val isLoading = MutableLiveData<Boolean>()
    private var job: Job? = null

    fun setStoreList(s :StoreInfo){
        storeList.value = s
    }
    fun getStoreList() : StoreInfo?{
        Log.d("로그", "getStoreList: ${storeList.value}")
        return storeList.value
    }
    private val exceptionHandler = CoroutineExceptionHandler { _, thrownException ->
        onError("코루틴내 예외: ${thrownException.localizedMessage}")
    }

    fun getStoreViewModel(areaName: String) {
        job = CoroutineScope(Dispatchers.IO).launch(exceptionHandler) {
            isLoading.postValue(true)

            var infoResponse = storeRepository.getStoreInfo(areaName) // 식당 자치구 정보
            Log.d("로그", "getStoreViewModel: 타니? $areaName")
//            if (guInfo != selectGuInfo){
//                storeRepository.getStoreInfo(selectGuInfo)
//            }
//            KakaoLocalRetrofitService.getInstance()
            withContext(Dispatchers.Main) {
                if (infoResponse.isSuccessful) {
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