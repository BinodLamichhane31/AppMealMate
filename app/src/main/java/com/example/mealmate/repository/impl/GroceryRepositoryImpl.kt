package com.example.mealmate.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

import com.example.mealmate.model.GroceryItem

class GroceryRepositoryImpl:GroceryRepository {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("grocery_items")

    override fun getGroceryList(callback: (List<GroceryItem>) -> Unit) {
        val userId = auth.currentUser?.uid ?: return
        database.child(userId).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<GroceryItem>()
                for (data in snapshot.children) {
                    val item = data.getValue(GroceryItem::class.java)
                    if (item != null) {
                        items.add(item)
                    }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    override fun addGroceryItem(item: GroceryItem) {
        val userId = auth.currentUser?.uid ?: return
        val itemId = database.child(userId).push().key ?: return
        val newItem = item.copy(id = itemId, userId = userId)
        database.child(userId).child(itemId).setValue(newItem)
    }

    override fun updateGroceryItem(item: GroceryItem) {
        val userId = auth.currentUser?.uid ?: return
        database.child(userId).child(item.id).setValue(item)
    }

    override fun deleteGroceryItem(itemId: String) {
        val userId = auth.currentUser?.uid ?: return
        database.child(userId).child(itemId).removeValue()
    }
    fun togglePurchasedStatus(item: GroceryItem) {
        val userId = auth.currentUser?.uid ?: return
        val updatedItem = item.copy(purchased = !item.purchased)
        database.child(userId).child(item.id).setValue(updatedItem)
    }
}
