package com.example.mealmate.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mealmate.R
import com.example.mealmate.model.Recipe
import com.example.mealmate.ui.home.RecipeDetailActivity
import com.example.mealmate.ui.home.UpdateRecipeActivity
import com.example.mealmate.utils.ImageUtils
import com.example.mealmate.viewmodel.RecipeViewModel

class MealPlanAdapter(private var recipes: List<Recipe>, private val viewModel: RecipeViewModel) :
    RecyclerView.Adapter<MealPlanAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipeName: TextView = view.findViewById(R.id.recipeNam)
        val tvRecipeCategory: TextView = view.findViewById(R.id.recCat)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.tvRecipeName.text = recipe.name
        holder.tvRecipeCategory.text = "Category: ${recipe.category}"

        if (recipe.image.isNotEmpty()) {
            val bitmap = ImageUtils.decodeBase64ToImage(recipe.image)
            bitmap?.let { holder.recipeImage.setImageBitmap(it) }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", recipe.id)
            holder.itemView.context.startActivity(intent)
        }


    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}

