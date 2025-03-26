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
import com.example.mealmate.model.MealPlan
import com.example.mealmate.model.Recipe
import com.example.mealmate.ui.home.RecipeDetailActivity
import com.example.mealmate.ui.home.UpdateRecipeActivity
import com.example.mealmate.utils.ImageUtils
import com.example.mealmate.viewmodel.MealPlanViewModel
import com.example.mealmate.viewmodel.RecipeViewModel

class MealPlanAdapter(private var mealPlans: List<MealPlan>, private val viewModel: MealPlanViewModel) :
    RecyclerView.Adapter<MealPlanAdapter.RecipeViewHolder>() {

    class RecipeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvRecipeName: TextView = view.findViewById(R.id.recipeNam)
        val tvRecipeCategory: TextView = view.findViewById(R.id.recCat)
        val recipeImage: ImageView = view.findViewById(R.id.recipeImg)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_meal, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val mealPlans = mealPlans[position]
        holder.tvRecipeName.text = mealPlans.recipe.name
        holder.tvRecipeCategory.text = "Category: ${mealPlans.recipe.category}"

        if (mealPlans.recipe.image.isNotEmpty()) {
            val bitmap = ImageUtils.decodeBase64ToImage(mealPlans.recipe.image)
            bitmap?.let { holder.recipeImage.setImageBitmap(it) }
        }

        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, RecipeDetailActivity::class.java)
            intent.putExtra("recipeId", mealPlans.recipe.id)
            holder.itemView.context.startActivity(intent)
        }
        holder.btnDelete.setOnClickListener {
            viewModel.deleteMealPlan(mealPlans.id,mealPlans.day) // Assuming you have a method to delete by ID
        }


    }

    override fun getItemCount(): Int = mealPlans.size

    fun updateData(newMeals: List<MealPlan>) {
        mealPlans = newMeals
        notifyDataSetChanged()
    }
}

