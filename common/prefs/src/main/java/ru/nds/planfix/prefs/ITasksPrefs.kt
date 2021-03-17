package ru.nds.planfix.prefs

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

interface ITasksPrefs {
    fun isScanned(): Boolean
    fun generateAuth(apiKey: String, token: String)
    var authHeader: String
    var account: String
    var robotName: String
    var generalTaskNumber: String
    var analiticId: String
    var analiticFieldId: String
}

class TasksPrefsStorage(
    context: Context
) : ITasksPrefs {

    private companion object {
        private const val COMMON_PREFS_NAME = "COMMON_PREFS_NAME"
        private const val AUTH_HEADER = "AUTH_HEADER"
        private const val ACCOUNT = "ACCOUNT"
        private const val ROBOT_NAME = "ROBOT_NAME"
        private const val GENERAL_TASK_NUMBER = "GENERAL_TASK_NUMBER"
        private const val ANALITIC_ID = "ANALITIC_ID"
        private const val ANALITIC_FIELD_ID = "ANALITIC_FIELD_ID"
    }

    private val prefs =
        context.applicationContext.getSharedPreferences(COMMON_PREFS_NAME, Context.MODE_PRIVATE)

    override fun isScanned(): Boolean {
        return authHeader.isNotBlank()
                && account.isNotBlank()
                && robotName.isNotBlank()
                && generalTaskNumber.isNotBlank()
                && analiticId.isNotBlank()
                && analiticFieldId.isNotBlank()
    }

    override fun generateAuth(apiKey: String, token: String) {
        val encoded = Base64.encodeToString("$apiKey:$token".toByteArray(), Base64.NO_WRAP)
        authHeader = "Basic $encoded"
    }
    override var authHeader: String
        get() = prefs.getStringOrEmpty(AUTH_HEADER)
        set(value) {
            prefs.writeString(AUTH_HEADER, value)
        }
    override var account: String
        get() = prefs.getStringOrEmpty(ACCOUNT)
        set(value) {
            prefs.writeString(ACCOUNT, value)
        }
    override var robotName: String
        get() = prefs.getStringOrEmpty(ROBOT_NAME)
        set(value) {
            prefs.writeString(ROBOT_NAME, value)
        }
    override var generalTaskNumber: String
        get() = prefs.getStringOrEmpty(GENERAL_TASK_NUMBER)
        set(value) {
            prefs.writeString(GENERAL_TASK_NUMBER, value)
        }
    override var analiticId: String
        get() = prefs.getStringOrEmpty(ANALITIC_ID)
        set(value) {
            prefs.writeString(ANALITIC_ID, value)
        }
    override var analiticFieldId: String
        get() = prefs.getStringOrEmpty(ANALITIC_FIELD_ID)
        set(value) {
            prefs.writeString(ANALITIC_FIELD_ID, value)
        }

    private fun SharedPreferences.getStringOrEmpty(name: String) = getString(name, "") ?: ""
    private fun SharedPreferences.writeString(name: String, value: String) {
        this.edit().putString(name, value).apply()
    }
}