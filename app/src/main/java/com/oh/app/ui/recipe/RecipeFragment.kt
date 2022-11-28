package com.oh.app.ui.recipe

import android.view.LayoutInflater
import android.view.ViewGroup
import com.oh.app.databinding.RecipeFragmentBinding
import com.oh.app.ui.base.BaseFragment

class RecipeFragment : BaseFragment<RecipeFragmentBinding>(){
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RecipeFragmentBinding {
        return RecipeFragmentBinding.inflate(inflater, container, false)
    }
}