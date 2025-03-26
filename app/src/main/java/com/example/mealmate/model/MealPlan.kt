package com.example.mealmate.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class MealPlan(
    val id: String = "",
    val recipe: Recipe = Recipe(),
    val day: String = "",
    val userId: String = ""
):Parcelable
