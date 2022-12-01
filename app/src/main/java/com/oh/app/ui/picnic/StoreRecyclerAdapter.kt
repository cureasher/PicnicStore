package com.oh.app.ui.picnic

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.oh.app.data.store.Row
import com.oh.app.databinding.RecipeInfoItemBinding

class PicnicViewHolder(val binding: RecipeInfoItemBinding) : RecyclerView.ViewHolder(binding.root)
class StoreRecyclerAdapter(private var recipeList: List<Row>) :
    RecyclerView.Adapter<PicnicViewHolder>() {
    private lateinit var binding: RecipeInfoItemBinding
    private val recipeItem = recipeList[0]
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PicnicViewHolder {
        binding = RecipeInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PicnicViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PicnicViewHolder, position: Int) {
        with(holder.binding) {
            Log.d("로그", "onBindViewHolder recipeItem: $recipeItem")
            recipeName.text = recipeItem.UPSO_NM
        }
    }

    override fun getItemCount() = recipeList.size
    fun refreshRecipeItems() {
        notifyItemChanged(0, recipeList.size)
    }
}