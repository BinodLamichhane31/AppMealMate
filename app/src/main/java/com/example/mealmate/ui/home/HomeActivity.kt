package com.example.mealmate.ui.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.mealmate.BaseActivity
import com.example.mealmate.R
import com.example.mealmate.ui.home.fragments.GroceryFragment
import com.example.mealmate.ui.home.fragments.MealFragment
import com.example.mealmate.ui.home.fragments.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        // Load the default fragment
        if (savedInstanceState == null) {
            replaceFragment(MealFragment())
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_meals -> replaceFragment(MealFragment())
                R.id.nav_grocery -> replaceFragment(GroceryFragment())
                R.id.nav_profile -> replaceFragment(ProfileFragment())
            }
            true
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)
            .commit()
    }
}
