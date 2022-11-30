import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oh.app.MainActivity
import com.oh.app.databinding.RecipeInfoItemBinding

class RecipeRecyclerAdapter(private var recipeList: RecipeInfoData, val owner: MainActivity) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    private lateinit var binding: RecipeInfoItemBinding
    private val list = recipeList.COOKRCP01.row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        binding = RecipeInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipeItem = list.get(position)

        with(holder.binding) {
            Log.d("로그", "onBindViewHolder: $recipeItem")
            recipeName.text = recipeItem.RCP_NM
            Glide.with(this.recipeImage.context).load("""${recipeItem.ATT_FILE_NO_MAIN}""")
                .into(this.recipeImage)
        }
    }

    override fun getItemCount() = list.size
    fun refreshRecipeItems() {
        notifyItemChanged(0, list.size)
    }
}