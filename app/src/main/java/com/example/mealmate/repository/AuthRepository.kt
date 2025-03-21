package com.example.mealmate.repository

import androidx.lifecycle.LiveData
import com.example.mealmate.utils.LoadingUtil
import com.google.firebase.auth.FirebaseUser

interface AuthRepository {
    suspend fun registerUser(email: String, password: String,name: String): FirebaseUser?
    suspend fun loginUser(email: String, password: String): FirebaseUser?
    fun logoutUser()
    fun getCurrentUser(): FirebaseUser?
    fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        loadingUtil: LoadingUtil
    ): LiveData<Result<String>>
    fun deleteUser(userId: String): LiveData<Result<Void?>>

}