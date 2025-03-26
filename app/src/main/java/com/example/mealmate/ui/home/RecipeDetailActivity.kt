package com.example.mealmate.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mealmate.BaseActivity
import com.example.mealmate.R
import com.example.mealmate.databinding.ActivityLoginBinding
import com.example.mealmate.databinding.ActivityRecipeDetailBinding
import com.example.mealmate.model.Recipe
import com.example.mealmate.utils.ImageUtils
import com.example.mealmate.viewmodel.RecipeViewModel

class RecipeDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding
    private val recipeViewModel: RecipeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipeId = intent.getStringExtra("recipeId") ?: return

        // Fetch the recipe by ID using ViewModel
        recipeViewModel.getRecipeById(recipeId)
        recipeViewModel.recipe.observe(this) { fetchedRecipe ->
            if (fetchedRecipe != null) {
                loadRecipeDetails(fetchedRecipe)
            } else {
                Toast.makeText(this, "Recipe not found!", Toast.LENGTH_SHORT).show()
                finish()
            }
        }

        binding.dpBack.setOnClickListener { finish() }
    }

    private fun loadRecipeDetails(recipe: Recipe) {
        binding.dpRecipeName.text = recipe.name
        binding.dpCategory.text = "Category: " + recipe.category
        binding.dpServing.text = "Serving: x" + recipe.serving
        binding.dpIngredients.text = recipe.ingredients.joinToString(", ")
        binding.dpInstruction.text = recipe.instructions
        if (recipe.image.isNotEmpty()) {
            val bitmap = ImageUtils.decodeBase64ToImage(recipe.image)
            bitmap?.let { binding.recipeImageRD.setImageBitmap(it) }
        }
    }
}
