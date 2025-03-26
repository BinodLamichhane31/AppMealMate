package com.example.mealmate.repository

import android.net.Uri
import com.example.mealmate.model.Recipe

interface RecipeRepository {
    fun addRecipe(recipe: Recipe, callback: (Boolean) -> Unit)
    fun updateRecipe(recipe: Recipe, callback: (Boolean) -> Unit)
    fun deleteRecipe(recipeId: String, callback: (Boolean) -> Unit)
    fun getRecipes(callback: (List<Recipe>) -> Unit)
    fun getRecipeById(recipeId: String, callback: (Recipe?) -> Unit)
}



