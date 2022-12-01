import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oh.app.ui.picnic.StoreViewModel
import com.oh.app.ui.picnic.repository.StoreRepository

@Suppress("UNCHECKED_CAST")
class StoreViewModelFactory(private var repository: StoreRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(StoreViewModel::class.java)){
            StoreViewModel(repository) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 찾을수 없습니다.")
        }
    }
}