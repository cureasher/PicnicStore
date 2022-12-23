package com.oh.app.ui.recipe.adapter

import RecipeViewHolder
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.oh.app.data.recipe.RecipeInfoData
import com.oh.app.databinding.RecipeInfoItemBinding
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.recipe.detail.RecipeDetailActivity

class RecipeRecyclerAdapter(recipeList: RecipeInfoData, private val owner: MainActivity) :
    RecyclerView.Adapter<RecipeViewHolder>() {
    private lateinit var binding: RecipeInfoItemBinding
    private val list = recipeList.cookRcp01.row
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        binding = RecipeInfoItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RecipeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipeItem = list[position]

        with(holder.binding) {
            recipeName.text = recipeItem.rcpNm
            Glide.with(this.recipeImage.context).load(recipeItem.attFileNoMain)
                .into(this.recipeImage)

            recipeImage.setOnClickListener {
                val recipeListDetailIntent = Intent(owner, RecipeDetailActivity::class.java)
                recipeListDetailIntent.putExtra("list", list)
                recipeListDetailIntent.putExtra("rcpName", recipeItem.rcpNm)
                recipeListDetailIntent.putExtra("rcpParts", recipeItem.rcpPartsDtls)
                recipeListDetailIntent.putExtra("rcpFile", recipeItem.attFileNoMain)
                recipeListDetailIntent.putExtra("manualIMG01", recipeItem.manualImg01)
                recipeListDetailIntent.putExtra("manualIMG02", recipeItem.manualImg02)
                recipeListDetailIntent.putExtra("manualIMG03", recipeItem.manualImg03)
                recipeListDetailIntent.putExtra("manualIMG04", recipeItem.manualImg04)
                recipeListDetailIntent.putExtra("manualIMG05", recipeItem.manualImg05)
                recipeListDetailIntent.putExtra("manual01", recipeItem.manual01)
                recipeListDetailIntent.putExtra("manual02", recipeItem.manual02)
                recipeListDetailIntent.putExtra("manual03", recipeItem.manual03)
                recipeListDetailIntent.putExtra("manual04", recipeItem.manual04)
                recipeListDetailIntent.putExtra("manual05", recipeItem.manual05)
                owner.startActivity(recipeListDetailIntent)
            }
        }
    }

    override fun getItemCount() = list.size
    fun refreshRecipeItems() {
        notifyItemChanged(0, list.size)
    }
}