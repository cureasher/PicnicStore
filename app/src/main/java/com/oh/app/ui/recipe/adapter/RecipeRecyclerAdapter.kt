import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oh.app.data.recipe.RecipeInfoData
import com.oh.app.databinding.RecipeInfoItemBinding
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.recipe.detail.RecipeDetailActivity

class RecipeRecyclerAdapter(private var recipeList: RecipeInfoData, val owner: MainActivity) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    private lateinit var binding: RecipeInfoItemBinding
    private val list = recipeList.COOKRCP01.row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        binding = RecipeInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipeItem = list[position]

        with(holder.binding) {
            recipeName.text = recipeItem.RCP_NM
            Glide.with(this.recipeImage.context).load("""${recipeItem.ATT_FILE_NO_MAIN}""")
                .into(this.recipeImage)

            recipeImage.setOnClickListener {
                var recipeListDetailIntent = Intent(owner, RecipeDetailActivity::class.java)
                recipeListDetailIntent.putExtra("list", list)
                recipeListDetailIntent.putExtra("rcpName", recipeItem.RCP_NM)
                recipeListDetailIntent.putExtra("rcpParts", recipeItem.RCP_PARTS_DTLS)
                recipeListDetailIntent.putExtra("rcpFile", recipeItem.ATT_FILE_NO_MAIN)
                recipeListDetailIntent.putExtra("manualIMG01", recipeItem.MANUAL_IMG01)
                recipeListDetailIntent.putExtra("manualIMG02", recipeItem.MANUAL_IMG02)
                recipeListDetailIntent.putExtra("manualIMG03", recipeItem.MANUAL_IMG03)
                recipeListDetailIntent.putExtra("manualIMG04", recipeItem.MANUAL_IMG04)
                recipeListDetailIntent.putExtra("manualIMG05", recipeItem.MANUAL_IMG05)
                recipeListDetailIntent.putExtra("manual01", recipeItem.MANUAL01)
                recipeListDetailIntent.putExtra("manual02", recipeItem.MANUAL02)
                recipeListDetailIntent.putExtra("manual03", recipeItem.MANUAL03)
                recipeListDetailIntent.putExtra("manual04", recipeItem.MANUAL04)
                recipeListDetailIntent.putExtra("manual05", recipeItem.MANUAL05)
                owner.startActivity(recipeListDetailIntent)
            }
        }
    }

    override fun getItemCount() = list.size
    fun refreshRecipeItems() {
        notifyItemChanged(0, list.size)
    }
}