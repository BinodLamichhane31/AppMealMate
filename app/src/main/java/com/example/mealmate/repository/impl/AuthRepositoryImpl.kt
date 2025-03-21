package com.example.mealmate.repository.impl

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.mealmate.repository.AuthRepository
import com.example.mealmate.utils.LoadingUtil
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl : AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()

    override suspend fun registerUser(email: String, password: String, name: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                val userMap = mapOf(
                    "userId" to user.uid,
                    "name" to name,
                    "email" to email
                )
                firebaseDatabase.reference.child("users").child(user.uid).setValue(userMap).await()
            }
            result.user
        } catch (e: FirebaseAuthException) {
            throw Exception(getFirebaseErrorMessage(e))
        } catch (e: Exception) {
            throw Exception("Registration failed. Please try again.")
        }
    }

    private fun getFirebaseErrorMessage(e: FirebaseAuthException): String {
        return when (e.errorCode) {
            "ERROR_INVALID_EMAIL" -> "Invalid email format."
            "ERROR_WEAK_PASSWORD" -> "Password is too weak. Use at least 6 characters."
            "ERROR_EMAIL_ALREADY_IN_USE" -> "This email is already registered."
            else -> "Authentication failed. Please check your details."
        }
    }



    override suspend fun loginUser(email: String, password: String): FirebaseUser? {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            result.user
        } catch (e: Exception) {
                throw Exception("Wrong credentials")

        }
    }


    override fun logoutUser() {
        firebaseAuth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override fun changePassword(
        currentPassword: String,
        newPassword: String,
        confirmNewPassword: String,
        loadingUtil: LoadingUtil
    ): LiveData<Result<String>> {
        val resultLiveData = MutableLiveData<Result<String>>()

        if (currentPassword.isNotEmpty() && newPassword.isNotEmpty() && confirmNewPassword.isNotEmpty()) {
            if (currentPassword != newPassword) {
                if (newPassword == confirmNewPassword) {
                    val user = firebaseAuth.currentUser
                    if (user != null && user.email != null) {
                        loadingUtil.showLoading()
                        val credential =
                            EmailAuthProvider.getCredential(user.email!!, currentPassword)
                        user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                            if (reAuthTask.isSuccessful) {
                                user.updatePassword(newPassword)
                                    .addOnCompleteListener { passwordChange ->
                                        if (passwordChange.isSuccessful) {
                                            resultLiveData.postValue(Result.success("Password Changed Successfully"))
                                        } else {
                                            resultLiveData.postValue(Result.failure(Exception("Failed to change password")))
                                        }
                                    }
                            } else {
                                loadingUtil.dismiss()
                                resultLiveData.postValue(Result.failure(Exception("Current password is wrong.")))
                            }
                        }
                    } else {
                        resultLiveData.postValue(Result.failure(Exception("User authentication failed")))
                    }
                } else {
                    resultLiveData.postValue(Result.failure(Exception("New password and confirm password do not match.")))
                }
            } else {
                resultLiveData.postValue(Result.failure(Exception("New password and current password cannot be similar.")))
            }
        } else {
            resultLiveData.postValue(Result.failure(Exception("Please fill all the fields.")))
        }

        return resultLiveData
    }

    @SuppressLint("SuspiciousIndentation")
    override fun deleteUser(userId: String): LiveData<Result<Void?>> {
        val result = MutableLiveData<Result<Void?>>()
        firebaseDatabase.reference.child("users").child(userId).removeValue()
            .addOnSuccessListener {
                result.value = Result.success(null)
            }
            .addOnFailureListener {
                result.value = Result.failure(it)
            }
        return result
    }
}
