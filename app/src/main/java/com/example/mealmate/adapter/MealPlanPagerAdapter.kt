package com.example.mealmate.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter

class MealPlanPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
    private val days = arrayOf("Sunday","Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")

    override fun getCount(): Int = days.size

    override fun getItem(position: Int): Fragment {
        return DayMealFragment.newInstance(days[position])
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return days[position]
    }
}