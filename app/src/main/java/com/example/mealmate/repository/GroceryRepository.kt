package com.example.mealmate.repository

import com.example.mealmate.model.GroceryItem
import com.google.firebase.database.*

interface GroceryRepository {
    fun addGroceryItem(item: GroceryItem)
    fun updateGroceryItem(item: GroceryItem)
    fun deleteGroceryItem(itemId: String)
    fun getGroceryList(callback: (List<GroceryItem>) -> Unit)
}
