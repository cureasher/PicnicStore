@file:Suppress("DEPRECATION", "InternalInsetResource", "DiscouragedApi")

package com.oh.app.ui.recipe.detail

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup.LayoutParams
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.bumptech.glide.Glide
import com.oh.app.common.TAG
import com.oh.app.data.recipe.RecipeInfoData
import com.oh.app.databinding.RecipeDetailActivityBinding

class RecipeDetailActivity : AppCompatActivity() {
    lateinit var binding: RecipeDetailActivityBinding

    private fun getStatusBarHeight(context: Context): Int {
        val screenSizeType: Int = context.resources.configuration.screenLayout and
                Configuration.SCREENLAYOUT_SIZE_MASK
        var statusbar = 0
        if (screenSizeType != Configuration.SCREENLAYOUT_SIZE_XLARGE) {
            val resourceId: Int =
                context.resources.getIdentifier("status_bar_height", "dimen", "android")
            if (resourceId > 0) {
                statusbar = context.resources.getDimensionPixelSize(resourceId)
            }
        }
        return statusbar
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RecipeDetailActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val constraintLayoutParam =
            ConstraintLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT)
        constraintLayoutParam.topMargin = getStatusBarHeight(this)

        Log.d(TAG, "onCreate: ${getStatusBarHeight(this)}")
        val detailIntent = intent
//
        val recipeDetailInfo = detailIntent.getParcelableArrayListExtra<RecipeInfoData>("list")
        val recipeName = detailIntent.getStringExtra("rcpName")
        val recipeParts = detailIntent.getStringExtra("rcpParts")
        val recipeFile = detailIntent.getStringExtra("rcpFile")
        val manualIMG01 = detailIntent.getStringExtra("manualIMG01")
        val manualIMG02 = detailIntent.getStringExtra("manualIMG02")
        val manualIMG03 = detailIntent.getStringExtra("manualIMG03")
        val manualIMG04 = detailIntent.getStringExtra("manualIMG04")
        val manual01 = detailIntent.getStringExtra("manual01")
        val manual02 = detailIntent.getStringExtra("manual02")
        val manual03 = detailIntent.getStringExtra("manual03")
        val manual04 = detailIntent.getStringExtra("manual04")


        Log.d(TAG, "recipeDetailInfo: $recipeDetailInfo")
        Log.d(TAG, "recipeName: $recipeName")
        Log.d(TAG, "recipeParts: $recipeParts")
        Log.d(TAG, "recipeFile: $recipeFile")
        Log.d(TAG, "manualIMG01: $manualIMG01")
        Log.d(TAG, "manualIMG02: $manualIMG02")
        Log.d(TAG, "manualIMG03: $manualIMG03")
        Log.d(TAG, "manualIMG04: $manualIMG04")
        Log.d(TAG, "manual01: $manual01")
        Log.d(TAG, "manual02: $manual02")
        Log.d(TAG, "manual03: $manual03")
        Log.d(TAG, "manual04: $manual04")

        with(binding) {
            recipeMenuName.text = recipeName
            Glide.with(this.recipeMainImage.context).load(recipeFile).into(this.recipeMainImage)
            recipeMaterialName.text = recipeParts
            Glide.with(this.guideImage1.context).load(manualIMG01).into(this.guideImage1)
            Glide.with(this.guideImage2.context).load(manualIMG02).into(this.guideImage2)
            Glide.with(this.guideImage3.context).load(manualIMG03).into(this.guideImage3)
            Glide.with(this.guideImage4.context).load(manualIMG04).into(this.guideImage4)

            guideText1.text = manual01
            guideText2.text = manual02
            guideText3.text = manual03
            guideText4.text = manual04
        }
    }
}