package com.example.mealmate.ui.home.fragments

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager.widget.ViewPager
import com.example.mealmate.R
import com.example.mealmate.adapter.MealPlanPagerAdapter
import com.example.mealmate.ui.auth.LoginActivity
import com.example.mealmate.viewmodel.MealPlanViewModel
import com.example.mealmate.viewmodel.RecipeViewModel
import com.google.android.material.tabs.TabLayout
import java.util.Calendar

class MealPlanFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout
    private lateinit var clearAll: TextView
    private val mealPlanViewModel: MealPlanViewModel by viewModels()




    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_meal_plan, container, false)

        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)
        clearAll = rootView.findViewById(R.id.clearAllMealPlan)

        clearAll.setOnClickListener{
            val builder = AlertDialog.Builder(requireContext())
            builder.setTitle("Clear All Plans")
            builder.setMessage("Are you sure you want to clear all the plans?")

            builder.setPositiveButton("Yes") { dialog, _ ->
                mealPlanViewModel.deleteAllMealPlan()
                Toast.makeText(context, "All plans cleared.", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            builder.setNegativeButton("No") { dialog, _ ->
                dialog.dismiss() // Dismiss the dialog if the user cancels
            }
            builder.create().show()

        }

        setupViewPager()

        return rootView
    }

    private fun setupViewPager() {
        val adapter = MealPlanPagerAdapter(childFragmentManager)
        viewPager.adapter = adapter
        tabLayout.setupWithViewPager(viewPager)

        // Get the current day of the week
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // Sunday = 1, Monday = 2, ..., Saturday = 7

        // Set the ViewPager to the current day (adjusting for zero-based index)
        viewPager.currentItem = currentDayOfWeek - 1
    }
}