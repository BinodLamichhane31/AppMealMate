package com.example.mealmate.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealmate.model.MealPlan
import com.example.mealmate.repository.impl.MealPlanRepositoryImpl

class MealPlanViewModel : ViewModel() {
    private val repository = MealPlanRepositoryImpl()

    private val _mealPlans = MutableLiveData<List<MealPlan>>()
    val mealPlans: LiveData<List<MealPlan>> get() = _mealPlans

    fun addMealPlan(mealPlan: MealPlan) {
        repository.addMealPlan(mealPlan) { success ->
            if (success) loadMealPlans(mealPlan.day)
        }
    }

    fun loadMealPlans(day: String) {
        repository.getMealPlans(day) { mealPlanList ->
            _mealPlans.postValue(mealPlanList)
        }
    }

    fun deleteMealPlan(mealPlanId: String, day: String) {
        repository.deleteMealPlan(mealPlanId) { success ->
            if (success) loadMealPlans(day)
        }
    }

    fun deleteAllMealPlan() {
        repository.deleteAllMealPlan() { success ->
            if (success)
                Log.d("Clear All","All Cleared")
        }
    }

}
