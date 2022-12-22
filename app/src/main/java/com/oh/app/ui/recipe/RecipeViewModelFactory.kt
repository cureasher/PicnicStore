import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.oh.app.ui.recipe.RecipeViewModel
import com.oh.app.ui.recipe.repository.RecipeRepository

@Suppress("UNCHECKED_CAST")
class RecipeViewModelFactory(private var repository: RecipeRepository): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(RecipeViewModel::class.java)){
            RecipeViewModel(repository) as T
        } else {
            throw IllegalArgumentException("해당 뷰모델 찾을수 없습니다.")
        }
    }
}