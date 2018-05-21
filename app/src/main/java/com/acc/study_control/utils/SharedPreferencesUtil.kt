package com.acc.study_control.utils

import android.content.Context
import android.preference.PreferenceManager

object SharedPreferencesUtil {
    /**
     * Set Preference Key
     */
    fun setPreference(context: Context, key: String, value: Any) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = sharedPreferences.edit()
        when (value) {
            is Int -> editor.putInt(key, value)
            is String -> editor.putString(key, value)
            is Boolean -> editor.putBoolean(key, value)
            is Long -> editor.putLong(key, value)
        }
        editor.commit()
    }

    /**
     * Get Integer Preference Key
     * @return Integer Preference
     */
    fun getIntegerPreference(context: Context, key: String) =
            PreferenceManager.getDefaultSharedPreferences(context).getInt(key, 0)

    /**
     * Get String Preference Key
     * @return String Preference
     */
    fun getStringPreference(context: Context, key: String) =
            PreferenceManager.getDefaultSharedPreferences(context).getString(key, "")

    /**
     * Get Boolean Preference Key
     * @return Boolean Preference
     */
    fun getBooleanPreference(context: Context, key: String) =
            PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, false)

    /**
     * Get Long Preference Key
     * @return Long Preference
     */
    fun getLongPreference(context: Context, key: String) =
            PreferenceManager.getDefaultSharedPreferences(context).getLong(key, 0)
}