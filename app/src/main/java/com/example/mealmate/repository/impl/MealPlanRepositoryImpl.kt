package com.example.mealmate.repository.impl

import com.example.mealmate.model.MealPlan
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MealPlanRepositoryImpl {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("mealplans")
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun addMealPlan(mealPlan: MealPlan, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        val day = mealPlan.day
        val recipeName = mealPlan.recipe.name // Assuming 'name' is unique for the recipe

        // Check if a meal plan with the same recipe for the same day already exists
        database.child(userId).orderByChild("day")
            .equalTo(day) // Find meal plans for the same day
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if there's an existing meal plan with the same recipe
                    val existingMealPlan = snapshot.children
                        .mapNotNull { it.getValue(MealPlan::class.java) }
                        .find { it.recipe.name == recipeName }

                    if (existingMealPlan != null) {
                        // Recipe already exists for the same day
                        callback(false)
                    } else {
                        // No duplicate recipe, add new meal plan
                        val id = database.push().key ?: return
                        val newMealPlan = mealPlan.copy(id = id, userId = userId)
                        database.child(userId).child(id).setValue(newMealPlan)
                            .addOnCompleteListener { callback(it.isSuccessful) }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }


    fun getMealPlans(day: String, callback: (List<MealPlan>) -> Unit) {
        val userId = auth.currentUser ?.uid ?: return
        database.child(userId).orderByChild("day").equalTo(day)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val mealPlans = snapshot.children.mapNotNull { it.getValue(MealPlan::class.java) }
                    callback(mealPlans)
                }
                override fun onCancelled(error: DatabaseError) {}
            })
    }
    fun deleteMealPlan(mealPlanId: String, callback: (Boolean) -> Unit) {
        val userId = auth.currentUser ?.uid ?: return
        database.child(userId).child(mealPlanId).removeValue()
            .addOnCompleteListener { callback(it.isSuccessful) }
    }
    fun deleteAllMealPlan(callback: (Boolean) -> Unit) {
        val userId = auth.currentUser ?.uid ?: return
        database.child(userId).removeValue()
            .addOnCompleteListener { callback(it.isSuccessful) }
    }
}