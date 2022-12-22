package com.oh.app.ui.picnic.mart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oh.app.ui.picnic.mart.repository.MartRepository

@Suppress("UNCHECKED_CAST")
class MartViewModelFactory(private var martRepository: MartRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MartViewModel::class.java)){
            MartViewModel(martRepository) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 찾을수 없습니다.")
        }
    }
}