package com.example.mealmate.ui.home.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.example.mealmate.R
import com.example.mealmate.repository.impl.AuthRepositoryImpl
import com.example.mealmate.ui.auth.LoginActivity
import com.example.mealmate.utils.LoadingUtil
import com.example.mealmate.viewmodel.AuthViewModel
import com.example.mealmate.viewmodel.AuthViewModelFactory
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class ProfileFragment : BaseFragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var fullNameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var logoutButton: Button
    private lateinit var changePsw: Button
    private lateinit var deleteAcc: Button

    private lateinit var viewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        auth = FirebaseAuth.getInstance()

        val repository = AuthRepositoryImpl()
        val factory = AuthViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[AuthViewModel::class.java]


        fullNameTextView = view.findViewById(R.id.fullNameTextView)
        emailTextView = view.findViewById(R.id.emailTextView)
        logoutButton = view.findViewById(R.id.logoutButton)
        changePsw = view.findViewById(R.id.changePasswordBtn)
        deleteAcc = view.findViewById(R.id.deleteAccountButton)




        val user = auth.currentUser
        if (user != null) {
            emailTextView.text = user.email ?: "No Email"

            // Load full name from SharedPreferences
            val sharedPreferences = activity?.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val fullName = sharedPreferences?.getString("fullName", "No Name")
            fullNameTextView.text = fullName ?: "No Name"
        }

        changePsw.setOnClickListener {
            showPasswordChangeDialog(requireContext(),viewModel,
                LoadingUtil(requireActivity())
            )
        }
        deleteAcc.setOnClickListener {
            showDeleteAccountDialog(requireContext(),viewModel,
                LoadingUtil(requireActivity())
            )
        }

        logoutButton.setOnClickListener {
            logout()
        }


        return view
    }

    fun showPasswordChangeDialog(
        context: Context, viewModel: AuthViewModel, loadingUtil: LoadingUtil
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.change_password_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)

        val currentPasswordEditText = dialog.findViewById<TextInputEditText>(R.id.currentPassword)
        val newPasswordEditText = dialog.findViewById<TextInputEditText>(R.id.newPassword)
        val confirmNewPasswordEditText = dialog.findViewById<TextInputEditText>(R.id.confirmPassword)
        val changeButton = dialog.findViewById<Button>(R.id.changePasswordButton)

        changeButton.setOnClickListener {
            val currentPassword = currentPasswordEditText.text.toString().trim()
            val newPassword = newPasswordEditText.text.toString().trim()
            val confirmNewPassword = confirmNewPasswordEditText.text.toString().trim()

            viewModel.changePassword(currentPassword, newPassword, confirmNewPassword, loadingUtil)
                .observe(context as LifecycleOwner) { result ->
                    result.fold(onSuccess = {
                        Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
                        if (it == "Password Changed Successfully") {
                            loadingUtil.dismiss()
                            dialog.dismiss()
                        }
                    }, onFailure = {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    })
                }
        }

        dialog.show()
    }

    fun showDeleteAccountDialog(
        context: Context, userViewModel: AuthViewModel, loadingUtil: LoadingUtil
    ) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.delete_account_dialog)
        dialog.window?.setBackgroundDrawableResource(R.drawable.custom_dialog_background)
        dialog.window?.setGravity(Gravity.CENTER)
        dialog.show()

        val passwordEditText = dialog.findViewById<EditText>(R.id.passwordEntry)
        val deleteButton = dialog.findViewById<Button>(R.id.deleteAccountButton)
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser

        deleteButton.setOnClickListener {
            val password = passwordEditText.text.toString()
            if (password.isNotEmpty() && user != null && user.email != null) {
                val credential = EmailAuthProvider.getCredential(user.email!!, password)
                loadingUtil.showLoading()
                user.reauthenticate(credential).addOnCompleteListener { reAuthTask ->
                    if (reAuthTask.isSuccessful) {
                        userViewModel.deleteUser(user.uid)
                            .observe(context as LifecycleOwner) { result ->
                                if (result.isSuccess) {
                                    user.delete().addOnCompleteListener { deleteTask ->
                                        if (deleteTask.isSuccessful) {
                                            Toast.makeText(
                                                context, "User account deleted.", Toast.LENGTH_SHORT
                                            ).show()
                                            loadingUtil.dismiss()
                                            dialog.dismiss()

                                            // Clear the login state in SharedPreferences
                                            val sharedPreferences = activity?.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
                                            val editor = sharedPreferences?.edit()
                                            editor?.putBoolean("isLoggedIn", false) // Set login state to false
                                            editor?.apply()

                                            // Navigate to LoginActivity after logout
                                            val intent = Intent(activity, LoginActivity::class.java)
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                            startActivity(intent)
                                        } else {
                                            loadingUtil.dismiss()
                                            Toast.makeText(
                                                context,
                                                "Failed to delete user account.",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }

                                } else {
                                    loadingUtil.dismiss()
                                    Toast.makeText(
                                        context,
                                        "Failed to delete teacher data.",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    } else {
                        loadingUtil.dismiss()
                        Toast.makeText(
                            context,
                            "Authentication failed. Please check your password.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            } else {
                Toast.makeText(context, "Please enter your password.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logout(){
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Logout user
            auth.signOut()

            // Clear the login state in SharedPreferences
            val sharedPreferences = activity?.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("isLoggedIn", false) // Set login state to false
            editor?.apply()

            // Navigate to LoginActivity after logout
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)

            dialog.dismiss() // Dismiss the dialog after the action
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss() // Dismiss the dialog if the user cancels
        }

        // Show the confirmation dialog
        builder.create().show()
    }

}
