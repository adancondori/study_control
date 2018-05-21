package com.acc.study_control.utils

import android.content.Context
import android.widget.Toast

object Utils {
    // General Utils Functions
    fun showToast(context: Context, message: String) =
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}