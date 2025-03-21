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
import com.example.mealmate.viewmodel.RecipeViewModel

class RecipeAdapter(private var recipes: List<Recipe>, private val viewModel: RecipeViewModel) :
    RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipeName: TextView = view.findViewById(R.id.recipeName)
        val tvRecipeCategory: TextView = view.findViewById(R.id.category)
        val btnEdit: ImageView = view.findViewById(R.id.rEdit)
        val btnDelete: ImageView = view.findViewById(R.id.rDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recipe_item, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val recipe = recipes[position]
        holder.tvRecipeName.text = recipe.name
        holder.tvRecipeCategory.text = "Category: ${recipe.category}"

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RecipeDetailActivity::class.java)
            intent.putExtra("recipe", recipe)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnEdit.setOnClickListener {
            val intent = Intent(holder.itemView.context, UpdateRecipeActivity::class.java)
            intent.putExtra("recipe", recipe)
            holder.itemView.context.startActivity(intent)
        }

        holder.btnDelete.setOnClickListener {
            val context = holder.itemView.context
            android.app.AlertDialog.Builder(context)
                .setTitle("Delete Recipe")
                .setMessage("Are you sure you want to delete ${recipe.name}'s recipe?")
                .setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteRecipe(recipe.id)
                }
                .setNegativeButton("No", null)
                .show()
        }
    }

    override fun getItemCount(): Int = recipes.size

    fun updateData(newRecipes: List<Recipe>) {
        recipes = newRecipes
        notifyDataSetChanged()
    }
}

