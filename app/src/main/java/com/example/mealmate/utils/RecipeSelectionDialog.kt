package com.example.mealmate.utils

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mealmate.R
import com.example.mealmate.adapter.DialogRecipeAdapter
import com.example.mealmate.viewmodel.MealPlanViewModel
import com.example.mealmate.viewmodel.RecipeViewModel

class RecipeSelectionDialog(private val viewModel: RecipeViewModel, private val day: String) : DialogFragment() {
    private val mealPlanViewModel: MealPlanViewModel by viewModels()



    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.dialog_recipe_selection, container, false)

        val listView: ListView = view.findViewById(R.id.recipeListView)
        val searchView: SearchView = view.findViewById(R.id.searchRecipeEditText)
        val adapter = DialogRecipeAdapter(requireContext(), emptyList(), day,mealPlanViewModel,this)
        listView.adapter = adapter

        viewModel.recipes.observe(viewLifecycleOwner) { recipes ->
            adapter.updateData(recipes)
        }

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = true

            override fun onQueryTextChange(newText: String?): Boolean {
                adapter.filter(newText ?: "")
                return true
            }
        })

        return view
    }
}
