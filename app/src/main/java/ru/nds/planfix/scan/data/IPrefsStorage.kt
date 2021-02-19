package ru.nds.planfix.scan.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

interface IPrefsStorage {
    fun generateAuth(apiKey: String, token: String)
    var authHeader: String
    var sid: String
    var taskId: String
    var analyticId: String
    var fieldId: String
    var account: String
    var userLogin: String
}

class PrefsStorage(
    private val context: Context
) : IPrefsStorage {
    private val prefs =
        context.applicationContext.getSharedPreferences(SETTINGS_PREFS_NAME, Context.MODE_PRIVATE)

    override fun generateAuth(apiKey: String, token: String) {
        authHeader = Base64.encodeToString("$apiKey:$token".toByteArray(), Base64.NO_WRAP)
    }

    override var authHeader: String
        get() = prefs.getStringOrEmpty(AUTH_HEADER)
        set(value) {
            prefs.writeString(AUTH_HEADER, value)
        }
    override var sid: String
        get() = prefs.getStringOrEmpty(SID)
        set(value) {
            prefs.writeString(SID, value)
        }
    override var taskId: String
        get() = prefs.getStringOrEmpty(TASK_ID)
        set(value) {
            prefs.writeString(TASK_ID, value)
        }
    override var analyticId: String
        get() = prefs.getStringOrEmpty(ANALYTIC_ID)
        set(value) {
            prefs.writeString(ANALYTIC_ID, value)
        }
    override var fieldId: String
        get() = prefs.getStringOrEmpty(FILE_ID)
        set(value) {
            prefs.writeString(FILE_ID, value)
        }
    override var account: String
        get() = prefs.getStringOrEmpty(ACCOUNT)
        set(value) {
            prefs.writeString(ACCOUNT, value)
        }
    override var userLogin: String
        get() = prefs.getStringOrEmpty(USER_LOGIN)
        set(value) {
            prefs.writeString(USER_LOGIN, value)
        }

    private fun SharedPreferences.getStringOrEmpty(name: String) = getString(name, "") ?: ""
    private fun SharedPreferences.writeString(name: String, value: String) {
        this.edit().putString(name, value).apply()
    }

    private companion object {
        private const val SETTINGS_PREFS_NAME = "SETTINGS_PREFS"
        private const val AUTH_HEADER = "AUTH_HEADER"
        private const val SID = "SID"
        private const val TASK_ID = "TASK_ID"
        private const val ANALYTIC_ID = "ANALYTIC_ID"
        private const val FILE_ID = "FILE_ID"
        private const val ACCOUNT = "ACCOUNT"
        private const val USER_LOGIN = "USER_LOGIN"
    }

}