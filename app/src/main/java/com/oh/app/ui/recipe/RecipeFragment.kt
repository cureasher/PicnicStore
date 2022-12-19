package com.oh.app.ui.recipe

import RecipeRecyclerAdapter
import RecipeRetrofitService
import RecipeViewModelFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.oh.app.R
import com.oh.app.databinding.RecipeFragmentBinding
import com.oh.app.ui.base.BaseFragment
import com.oh.app.ui.main.MainActivity
import com.oh.app.ui.recipe.repository.RecipeRepository

class RecipeFragment : BaseFragment<RecipeFragmentBinding>() {
    private lateinit var selectChip: String
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RecipeFragmentBinding {
        return RecipeFragmentBinding.inflate(inflater, container, false)
    }

    companion object {
        fun newInstance(param: String): Fragment {
            val fragment: Fragment = RecipeFragment()
            val bundle = Bundle()
            bundle.putString("title", param)
            fragment.arguments = bundle
            return fragment
        }
    }

    init {
        selectChip = "김치"
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: RecipeViewModel = ViewModelProvider(
            this, RecipeViewModelFactory(
                RecipeRepository(RecipeRetrofitService.getInstance())
            )
        ).get(RecipeViewModel::class.java)
        // 초기 세팅
        viewModel.getRecipeViewModel(selectChip ?: "김치")
        with(binding) {
            recipeChip.isSingleSelection = true

            kimchiChip.setTextColor(Color.BLACK)
            chickenChip.setTextColor(Color.BLACK)
            cabbageChip.setTextColor(Color.BLACK)
            recipeChip.setOnCheckedChangeListener { group, checkedId ->

                selectChip = group.children.toList().filter { (it as Chip).isChecked }
                    .joinToString(", ") { (it as Chip).text }
                Log.d("로그", "onViewCreated1: $selectChip")
                viewModel.getRecipeViewModel(selectChip ?: "김치")
            }
        }


        viewModel.recipeList.observe(viewLifecycleOwner) {
            with(binding.recipeList) {
                run {
                    var activity = activity
                    val recipeAdapter = RecipeRecyclerAdapter(it, activity as MainActivity)
                    adapter = recipeAdapter
                    recipeAdapter
                }.refreshRecipeItems()
            }
            binding.progressBar.visibility = View.GONE // 로딩
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) {
            binding.progressBar.progress = View.GONE
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            Log.e("로그", it)
        }

        viewModel.isLoading.observe(requireActivity(), Observer {
            if (it) {
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
        })

    }
}