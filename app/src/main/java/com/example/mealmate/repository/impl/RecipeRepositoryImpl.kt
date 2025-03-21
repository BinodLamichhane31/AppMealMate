package com.example.mealmate.repository.impl

import com.example.mealmate.model.Recipe
import com.example.mealmate.repository.RecipeRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class RecipeRepositoryImpl : RecipeRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("recipes")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun addRecipe(recipe: Recipe, callback: (Boolean) -> Unit) {
        val recipeId = database.push().key ?: return
        val userId = auth.currentUser?.uid ?: return
        val newRecipe = recipe.copy(id = recipeId, userId = userId) // Assign userId to recipe

        database.child(recipeId).setValue(newRecipe)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }

    override fun updateRecipe(recipe: Recipe, callback: (Boolean) -> Unit) {
        if (recipe.id.isEmpty()) return

        database.child(recipe.id).setValue(recipe)
            .addOnCompleteListener { callback(it.isSuccessful) }
    }

    override fun deleteRecipe(recipeId: String, callback: (Boolean) -> Unit) {
        database.child(recipeId).removeValue()
            .addOnCompleteListener { callback(it.isSuccessful) }
    }

    override fun getRecipes(callback: (List<Recipe>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return

        database.orderByChild("userId").equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val recipes = snapshot.children.mapNotNull { it.getValue(Recipe::class.java) }
                    callback(recipes)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }
}
