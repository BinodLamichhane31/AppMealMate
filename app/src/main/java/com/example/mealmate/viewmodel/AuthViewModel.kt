package com.example.mealmate.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mealmate.repository.AuthRepository
import com.example.mealmate.utils.LoadingUtil
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.launch

class AuthViewModel(private val repository: AuthRepository): ViewModel() {
    private val _user = MutableLiveData<FirebaseUser?>()
    val user: LiveData<FirebaseUser?> get() = _user

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> get() = _error

    fun register(email: String, password: String,name: String) {
        viewModelScope.launch {
            try{
                val newUser = repository.registerUser(email, password, name)
                if (newUser != null) {
                    _user.value = newUser

            }

            } catch (e: Exception){
                _error.value = e.message
            }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            try {
                val loginUser = repository.loginUser(email, password)
                if (loginUser != null) {
                    _user.value = loginUser
                }
            } catch (e: Exception) {
                _error.value = e.message // Display specific error message like "Wrong credentials"
            }
        }
    }



    fun logout() {
        repository.logoutUser()
        _user.value = null
    }

    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        loadingUtil: LoadingUtil
    ): LiveData<Result<String>> {
        return repository.changePassword(currentPassword, newPassword, confirmNewPassword,loadingUtil)
    }

    fun deleteUser(userId: String): LiveData<Result<Void?>> {
        return repository.deleteUser(userId)
    }
}