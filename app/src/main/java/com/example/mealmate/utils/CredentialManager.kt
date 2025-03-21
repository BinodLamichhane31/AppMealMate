package com.example.mealmate.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

class CredentialManager(private val context: Context) {

    private val authSharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences("auth_state", Context.MODE_PRIVATE)
    }

    // Saves the login state: true for login, false for logout
    fun saveLoginState(isLoggedIn: Boolean) {
        with(authSharedPreferences.edit()) {
            putBoolean("isLoggedIn", isLoggedIn)
            apply()
        }
        Log.d("CredentialManager", "saveLoginState: Login state saved: $isLoggedIn")
    }

    // Checks the login state and returns true if logged in, false otherwise
    fun isLoggedIn(): Boolean {
        val loggedIn = authSharedPreferences.getBoolean("isLoggedIn", false)
        Log.d("CredentialManager", "isLoggedIn: Login state retrieved: $loggedIn")
        return loggedIn
    }


}
