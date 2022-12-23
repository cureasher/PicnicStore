package com.oh.app.ui.recipe

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.oh.app.common.TAG
import com.oh.app.common.SELECT_CHIP
import com.oh.app.common.toastMessage
import com.oh.app.databinding.RecipeFragmentBinding
import com.oh.app.rest.setting.RetrofitGenerator
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.recipe.adapter.RecipeRecyclerAdapter
import com.oh.app.ui.recipe.repository.RecipeRepository

/**
 * 음식 레시피 화면
 */
class RecipeFragment : BaseFragment<RecipeFragmentBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RecipeFragmentBinding {
        return RecipeFragmentBinding.inflate(inflater, container, false)
    }

    private lateinit var viewModel: RecipeViewModel
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModelSetting()
        chipSetting()
        viewModelObserve()
    }

    private fun viewModelSetting() {
        // 뷰모델 세팅
        viewModel = ViewModelProvider(
            this, RecipeViewModelFactory(
                RecipeRepository(RetrofitGenerator.getRecipeInstance())
            )
        )[RecipeViewModel::class.java]
        // 초기 세팅
        viewModel.getRecipeViewModel(SELECT_CHIP)
    }

    /**
     * 김치, 치킨, 양배추 칩 세팅
     */
    private fun chipSetting() {
        with(binding) {
            recipeChip.isSingleSelection = true
            kimchiChip.setTextColor(Color.WHITE)
            chickenChip.setTextColor(Color.BLACK)
            cabbageChip.setTextColor(Color.BLACK)
            recipeChip.setOnCheckedStateChangeListener { group, _ ->
                SELECT_CHIP = group.children.toList().filter {
                    (it as Chip).isChecked
                }.joinToString(", ") {
                    kimchiChip.setTextColor(Color.BLACK)
                    chickenChip.setTextColor(Color.BLACK)
                    cabbageChip.setTextColor(Color.BLACK)
                    if ((it as Chip).isChecked) {
                        it.setTextColor(Color.WHITE)
                    }
                    it.text
                }
                Log.d(TAG, "선택한 칩: $SELECT_CHIP")
                viewModel.getRecipeViewModel(SELECT_CHIP)
            }
        }
    }

    /**
     * 레시피 리스트 관찰 Observe
     */
    private fun viewModelObserve() {
        // 레시피 리스트 뷰모델에 관찰 시작 뷰모델로 전송하는 것
        viewModel.recipeList.observe(viewLifecycleOwner) {
            with(binding.recipeList) {
                run {
                    val activity = activity
                    val recipeAdapter = RecipeRecyclerAdapter(it, activity as MainActivity)
                    adapter = recipeAdapter
                    recipeAdapter
                }.refreshRecipeItems()
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding.progressBar.progress = View.GONE
            toastMessage(it)
            Log.e(TAG, it)
        }

        viewModel.isLoading.observe(requireActivity()) {
            if (it) {
                binding.recipeList.visibility = View.INVISIBLE
                binding.shimmerLoading.visibility = View.VISIBLE
            } else {
                binding.recipeList.visibility = View.VISIBLE
                binding.shimmerLoading.visibility = View.GONE
            }
        }
    }
}