package com.example.mealmate.ui.home

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.mealmate.BaseActivity
import com.example.mealmate.R
import com.example.mealmate.databinding.ActivityLoginBinding
import com.example.mealmate.databinding.ActivityRecipeDetailBinding
import com.example.mealmate.model.Recipe

class RecipeDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityRecipeDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecipeDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recipe = intent.getParcelableExtra<Recipe>("recipe")

        recipe?.let {
            binding.dpRecipeName.text = it.name
            binding.dpCategory.text = "Category: "+it.category
            binding.dpServing.text = "Serving: x"+it.serving
            binding.dpIngredients.text = it.ingredients.toString()
            binding.dpInstruction.text = it.instructions
        }

        binding.dpBack.setOnClickListener { finish() }
    }
}