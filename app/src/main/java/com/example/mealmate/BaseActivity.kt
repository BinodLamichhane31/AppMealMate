package com.example.mealmate
import ShakeDetector
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.mealmate.ui.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth

abstract class BaseActivity : AppCompatActivity() {
    private lateinit var shakeDetector: ShakeDetector
    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()

        shakeDetector = ShakeDetector(this) {
            logout()

        }
    }

    override fun onResume() {
        super.onResume()
        shakeDetector.start()
    }

    override fun onPause() {
        super.onPause()
        shakeDetector.stop()
    }

    private fun logout() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Logout")
        builder.setMessage("Are you sure you want to logout?")

        builder.setPositiveButton("Yes") { dialog, _ ->
            // Logout user
            auth.signOut()

            // Clear the login state in SharedPreferences
            val sharedPreferences = this.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences?.edit()
            editor?.putBoolean("isLoggedIn", false) // Set login state to false
            editor?.apply()

            // Navigate to LoginActivity after logout
            val intent = Intent(this, LoginActivity::class.java)
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
