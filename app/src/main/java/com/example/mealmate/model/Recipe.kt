package com.example.mealmate.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Recipe(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val ingredients: List<String> = listOf(),
    val instructions: String = "",
    val serving: String = "",
    val userId: String = ""
): Parcelable

