package com.example.mealmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mealmate.model.GroceryItem
import com.example.mealmate.repository.GroceryRepository
import com.example.mealmate.repository.GroceryRepositoryImpl

class GroceryViewModel : ViewModel() {
    private val repository = GroceryRepositoryImpl()

    private val _groceryItems = MutableLiveData<List<GroceryItem>>()
    val groceryItems: LiveData<List<GroceryItem>> get() = _groceryItems

    fun loadGroceryItems() {
        repository.getGroceryList { items ->
            _groceryItems.postValue(items)
        }
    }

    fun addGroceryItem(name: String, quantity: String) {
        if (name.isNotEmpty() && quantity.isNotEmpty()) {
            val item = GroceryItem(name = name, quantity = quantity)
            repository.addGroceryItem(item)
        }
    }

    fun updateGroceryItem(item: GroceryItem) {
        repository.updateGroceryItem(item)
    }

    fun deleteGroceryItem(itemId: String) {
        repository.deleteGroceryItem(itemId)
    }
    fun togglePurchasedStatus(item: GroceryItem) {
        repository.togglePurchasedStatus(item)
    }
}
