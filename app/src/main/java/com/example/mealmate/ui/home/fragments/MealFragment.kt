package com.example.mealmate.ui.home.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.adapter.RecipeAdapter
import com.example.mealmate.ui.home.AddRecipeActivity
import com.example.mealmate.viewmodel.RecipeViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MealFragment : BaseFragment() {

    private val recipeViewModel: RecipeViewModel by viewModels()
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var searchView: SearchView
    private lateinit var progressBar: ProgressBar


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_meal, container, false)

        recyclerView = rootView.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(requireContext(),2)

        searchView = rootView.findViewById(R.id.searchView)
        progressBar = rootView.findViewById(R.id.progressBar)


        recipeAdapter = RecipeAdapter(emptyList(), recipeViewModel)
        recyclerView.adapter = recipeAdapter

        progressBar.visibility = View.VISIBLE

        recipeViewModel.recipes.observe(viewLifecycleOwner, { recipes ->
            progressBar.visibility = View.GONE  // Hide when data is loaded

            recipeAdapter.updateData(recipes)
        })

        val addRecipeButton: FloatingActionButton = rootView.findViewById(R.id.addRecipeButton)
        addRecipeButton.setOnClickListener {
            val intent = Intent(requireContext(), AddRecipeActivity::class.java)
            startActivity(intent)
        }

        recipeViewModel.loadRecipes()

        setupSearch()

        return rootView
    }

    private fun setupSearch() {
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    filterRecipes(it)
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    filterRecipes(it)
                }
                return true
            }
        })
    }

    private fun filterRecipes(query: String) {
        val filteredList = recipeViewModel.recipes.value?.filter { recipe ->
            recipe.name.contains(query, ignoreCase = true) ||
                    recipe.category.contains(query, ignoreCase = true) ||
                    recipe.ingredients.any { it.contains(query, ignoreCase = true) }
        } ?: emptyList()

        recipeAdapter.updateData(filteredList)
    }
}
