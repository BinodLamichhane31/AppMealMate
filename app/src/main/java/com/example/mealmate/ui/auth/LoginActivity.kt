package com.example.mealmate.ui.auth

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.mealmate.databinding.ActivityLoginBinding
import com.example.mealmate.repository.impl.AuthRepositoryImpl
import com.example.mealmate.ui.home.HomeActivity
import com.example.mealmate.utils.LoadingUtil
import com.example.mealmate.viewmodel.AuthViewModel
import com.example.mealmate.viewmodel.AuthViewModelFactory
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingUtil: LoadingUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingUtil = LoadingUtil(this)

        val repository = AuthRepositoryImpl()
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]

        auth = FirebaseAuth.getInstance()

        binding.signInButton.setOnClickListener {
            val email = binding.usernameField.text.toString().trim()
            val password = binding.passwordField.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loadingUtil.showLoading()
                viewModel.login(email, password)
            } else {
                Toast.makeText(this, "Enter email & password", Toast.LENGTH_SHORT).show()
            }
        }

        binding.signUpButton.setOnClickListener {
            startActivity(Intent(this, RegistrationActivity::class.java))
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                val userId = user.uid
                val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()

                val databaseRef = FirebaseDatabase.getInstance().getReference("users")
                databaseRef.child(userId).child("name").get()
                    .addOnSuccessListener { snapshot ->
                        val fullName = snapshot.value?.toString() ?: "No Name"
                        editor.putString("fullName", fullName)
                        editor.putBoolean("isLoggedIn", true)
                        editor.apply()

                        loadingUtil.dismiss()
                        Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()

                        startActivity(Intent(this, HomeActivity::class.java))
                        finish()
                    }
                    .addOnFailureListener {
                        loadingUtil.dismiss() // Dismiss loading even if database fetch fails
                        Toast.makeText(this, "Failed to fetch user data", Toast.LENGTH_SHORT).show()
                    }
            }
        }

        viewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                loadingUtil.dismiss() // Dismiss loading if login fails
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()

        val sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }
    }
}
