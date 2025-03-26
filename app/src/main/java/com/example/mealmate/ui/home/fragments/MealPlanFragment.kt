package com.example.mealmate.ui.home.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.example.mealmate.R
import com.example.mealmate.adapter.MealPlanPagerAdapter
import com.google.android.material.tabs.TabLayout
import java.util.Calendar

class MealPlanFragment : Fragment() {

    private lateinit var viewPager: ViewPager
    private lateinit var tabLayout: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_meal_plan, container, false)

        viewPager = rootView.findViewById(R.id.viewPager)
        tabLayout = rootView.findViewById(R.id.tabLayout)

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