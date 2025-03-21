package com.example.mealmate.utils

import android.app.Activity
import android.app.AlertDialog
import android.util.TypedValue
import androidx.core.content.ContextCompat
import com.example.mealmate.R

class LoadingUtil(val activity: Activity) {
    private lateinit var dialog: AlertDialog

    fun showLoading() {
        val dialogView = activity.layoutInflater.inflate(R.layout.progress_bar, null)

        val builder = AlertDialog.Builder(activity)
        builder.setView(dialogView)
        builder.setCancelable(false)
        dialog = builder.create()
        val backgroundDrawable =
            ContextCompat.getDrawable(activity, R.drawable.custom_dialog_background)
        dialog.window?.setBackgroundDrawable(backgroundDrawable)
        dialog.show()

        val widthInDp = 150
        val heightInDp = 150
        val widthInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, widthInDp.toFloat(), activity.resources.displayMetrics).toInt()
        val heightInPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, heightInDp.toFloat(), activity.resources.displayMetrics).toInt()

        dialog.window?.setLayout(widthInPx, heightInPx)
    }

    fun dismiss() {
        dialog.dismiss()
    }
}