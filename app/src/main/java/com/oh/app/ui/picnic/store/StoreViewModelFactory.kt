package com.oh.app.ui.picnic.store

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oh.app.ui.picnic.store.repository.StoreRepository

class StoreViewModelFactory(private var storeRepository: StoreRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(StoreViewModel::class.java)){
            StoreViewModel(storeRepository) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 찾을수 없습니다.")
        }
    }
}