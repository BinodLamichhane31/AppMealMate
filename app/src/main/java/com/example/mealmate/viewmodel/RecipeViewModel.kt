package com.example.mealmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealmate.model.Recipe
import com.example.mealmate.repository.RecipeRepository
import com.example.mealmate.repository.impl.RecipeRepositoryImpl
import kotlinx.coroutines.launch

class RecipeViewModel : ViewModel() {
    private val repository = RecipeRepositoryImpl()

    private val _recipes = MutableLiveData<List<Recipe>>()
    val recipes: LiveData<List<Recipe>> get() = _recipes

    fun loadRecipes() {
        repository.getRecipes { recipeList ->
            _recipes.postValue(recipeList)
        }
    }

    fun addRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repository.addRecipe(recipe) { success ->
                if (success) loadRecipes()
            }
        }
    }
    fun updateRecipe(recipe: Recipe) {
        repository.updateRecipe(recipe) { success ->
            if (success) loadRecipes()
        }
    }

    fun deleteRecipe(recipeId: String) {
        repository.deleteRecipe(recipeId) { success ->
            if (success) loadRecipes()
        }
    }

}
