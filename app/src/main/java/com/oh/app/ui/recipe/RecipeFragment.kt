package com.oh.app.ui.recipe

import RecipeRecyclerAdapter
import RecipeRepository
import RecipeRetrofitService
import RecipeViewModelFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.oh.app.MainActivity
import com.oh.app.databinding.RecipeFragmentBinding
import com.oh.app.ui.base.BaseFragment

class RecipeFragment : BaseFragment<RecipeFragmentBinding>() {
    override fun getFragmentBinding(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): RecipeFragmentBinding {
        return RecipeFragmentBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val viewModel: RecipeViewModel = ViewModelProvider(
            this, RecipeViewModelFactory(
                RecipeRepository(RecipeRetrofitService.getInstance())
            )
        ).get(RecipeViewModel::class.java)


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
        viewModel.getRecipeViewModel()
    }
}