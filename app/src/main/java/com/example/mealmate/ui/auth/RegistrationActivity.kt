package com.example.mealmate.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.mealmate.databinding.ActivityRegisterationBinding
import com.example.mealmate.repository.impl.AuthRepositoryImpl
import com.example.mealmate.utils.LoadingUtil
import com.example.mealmate.viewmodel.AuthViewModel
import com.example.mealmate.viewmodel.AuthViewModelFactory

class RegistrationActivity: AppCompatActivity() {
    private lateinit var binding: ActivityRegisterationBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var loadingUtil: LoadingUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        loadingUtil = LoadingUtil(this)


        enableEdgeToEdge()
        val repository = AuthRepositoryImpl() // Ensure you have an implementation of AuthRepository
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        binding.signupRegButton.setOnClickListener {
            val name = binding.nameRegField.text.toString().trim()
            val email = binding.emailRegField.text.toString().trim()
            val password = binding.passwordRegField.text.toString().trim()
            val confirmPassword = binding.confirmPassField.text.toString().trim()

            if (validateInputs(name, email, password, confirmPassword)) {
                loadingUtil.showLoading()
                viewModel.register(email, password,name)
            }
        }
        binding.loginBtnSignupPage.setOnClickListener{
            startActivity(Intent(this,LoginActivity::class.java))
        }

        viewModel.user.observe(this) { user ->
            if (user != null) {
                loadingUtil.dismiss()
                Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }

        viewModel.error.observe(this) { error ->
            if (!error.isNullOrEmpty()) {
                loadingUtil.dismiss()
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun validateInputs(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            showToast("Please enter your name")
            return false
        }
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showToast("Please enter a valid email")
            return false
        }
        if (!isValidPassword(password)) {
            showToast("Password must be at least 8 characters, include a number, special character, and uppercase letter")
            return false
        }
        if (password != confirmPassword) {
            showToast("Passwords do not match")
            return false
        }
        return true
    }

    private fun isValidPassword(password: String): Boolean {
        val passwordPattern = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@\$!%*?&])[A-Za-z\\d@\$!%*?&]{8,}$"
        return password.matches(passwordPattern.toRegex())
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun enableEdgeToEdge() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content)) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

}