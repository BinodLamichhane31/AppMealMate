package com.example.mealmate.model

data class GroceryItem(
    val id: String = "",
    val userId: String = "",
    val name: String = "",
    val quantity: String = "",
    val purchased: Boolean = false
)
