package com.example.mealmate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.example.mealmate.R
import com.example.mealmate.model.MealPlan
import com.example.mealmate.model.Recipe
import com.example.mealmate.utils.RecipeSelectionDialog
import com.example.mealmate.viewmodel.GroceryViewModel
import com.example.mealmate.viewmodel.MealPlanViewModel

class DialogRecipeAdapter(
    context: Context,
    private var recipes: List<Recipe>,
    private val day: String,
    private val mealPlanViewModel: MealPlanViewModel,
    private val dialogFragment: DialogFragment
) : ArrayAdapter<Recipe>(context, 0, recipes) {

    private var filteredRecipes = recipes.toMutableList()


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.dialog_recipe_item, parent, false)
        val recipe = filteredRecipes[position]

        val nameTextView = view.findViewById<TextView>(R.id.recipeNameTextView)
        nameTextView.text = recipe.name

        // Log details when an item is clicked, including the day
        view.setOnClickListener {
            val mealPlan = MealPlan(recipe = recipe, day = day) // Replace "Monday" with the actual day
            mealPlanViewModel.addMealPlan(mealPlan)
            dialogFragment.dismiss()

        }

        return view
    }

    override fun getCount(): Int = filteredRecipes.size

    override fun getItem(position: Int): Recipe? = filteredRecipes[position]

    fun updateData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        filteredRecipes = newRecipes.toMutableList()
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredRecipes = if (query.isEmpty()) {
            recipes.toMutableList()
        } else {
            recipes.filter { it.name.contains(query, ignoreCase = true) }.toMutableList()
        }
        notifyDataSetChanged()
    }
}
