package com.nagarnikay.up_dbl.Utils



import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

object MySharedPreferences {

    private const val PREF_NAME = "MyPrefs"
    private const val KEY_OBJECT = "loginObj"

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }

    fun saveLoginObject(context: Context, `object`: Any) {
        val editor = getSharedPreferences(context).edit()
        val gson = Gson()
        val json = gson.toJson(`object`)
        editor.putString(KEY_OBJECT, json)
        editor.apply()
    }

    fun <T> getLoginObject(context: Context, classOfT: Class<T>): T? {
        val gson = Gson()
        val json = getSharedPreferences(context).getString(KEY_OBJECT, null)
        return json?.let { gson.fromJson(it, classOfT) }
    }
}
