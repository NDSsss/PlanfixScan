package ru.nds.planfix.prefs

import android.content.Context
import android.content.SharedPreferences

interface ICommonPrefs {
    var apiKey: String
    var token: String
    var account: String
}

class CommonPrefsStorage(
    context: Context
) : ICommonPrefs {

    private companion object {
        private const val COMMON_PREFS_NAME = "COMMON_PREFS_NAME"
        private const val API_KEY = "API_KEY"
        private const val TOKEN = "TOKEN"
        private const val ACCOUNT = "ACCOUNT"
    }

    private val prefs =
        context.applicationContext.getSharedPreferences(COMMON_PREFS_NAME, Context.MODE_PRIVATE)

    override var apiKey: String
        get() = prefs.getStringOrEmpty(API_KEY)
        set(value) {
            prefs.writeString(API_KEY, value)
        }
    override var token: String
        get() = prefs.getStringOrEmpty(TOKEN)
        set(value) {
            prefs.writeString(TOKEN, value)
        }
    override var account: String
        get() = prefs.getStringOrEmpty(ACCOUNT)
        set(value) {
            prefs.writeString(ACCOUNT, value)
        }

    private fun SharedPreferences.getStringOrEmpty(name: String) = getString(name, "") ?: ""
    private fun SharedPreferences.writeString(name: String, value: String) {
        this.edit().putString(name, value).apply()
    }
}