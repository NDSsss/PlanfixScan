package ru.nds.planfix.scan.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64

interface IPrefsStorage {
    fun generateAuth(apiKey: String, token: String)
    var account: String
    var userLogin: String
    var authHeader: String
    var sid: String
    var taskId: String
    var analyticId: String
    var fieldOneId: String
    var fieldTwoId: String
    var fieldThreeId: String
    var contactId: String
}

class ProductsPrefs(
    context: Context
):PrefsStorage(
    context,
    SETTINGS_PREFS_NAME
) {
    companion object {
        private const val SETTINGS_PREFS_NAME = "SETTINGS_PREFS"
    }
}

class StagesPrefs(
    context: Context
):PrefsStorage(
    context,
    SETTINGS_PREFS_NAME
) {
    companion object {
        private const val SETTINGS_PREFS_NAME = "SETTINGS_PREFS"
    }
}

open class PrefsStorage(
    private val context: Context,
    private val prefsName: String
) : IPrefsStorage {
    private val prefs =
        context.applicationContext.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override fun generateAuth(apiKey: String, token: String) {
        authHeader = Base64.encodeToString("$apiKey:$token".toByteArray(), Base64.NO_WRAP)
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
    override var contactId: String
        get() = prefs.getStringOrEmpty(CONTACT_ID)
        set(value) {
            prefs.writeString(CONTACT_ID, value)
        }
    override var analyticId: String
        get() = prefs.getStringOrEmpty(ANALYTIC_ID)
        set(value) {
            prefs.writeString(ANALYTIC_ID, value)
        }
    override var fieldOneId: String
        get() = prefs.getStringOrEmpty(FIELD_ONE_ID)
        set(value) {
            prefs.writeString(FIELD_ONE_ID, value)
        }

    override var fieldTwoId: String
        get() = prefs.getStringOrEmpty(FIELD_TWO_ID)
        set(value) {
            prefs.writeString(FIELD_TWO_ID, value)
        }

    override var fieldThreeId: String
        get() = prefs.getStringOrEmpty(FIELD_THREE_ID)
        set(value) {
            prefs.writeString(FIELD_THREE_ID, value)
        }

    private fun SharedPreferences.getStringOrEmpty(name: String) = getString(name, "") ?: ""
    private fun SharedPreferences.writeString(name: String, value: String) {
        this.edit().putString(name, value).apply()
    }

    private companion object {
        private const val ACCOUNT = "ACCOUNT"
        private const val USER_LOGIN = "USER_LOGIN"
        private const val AUTH_HEADER = "AUTH_HEADER"
        private const val SID = "SID"
        private const val TASK_ID = "TASK_ID"
        private const val ANALYTIC_ID = "ANALYTIC_ID"
        private const val CONTACT_ID = "CONTACT_ID"
        private const val FIELD_ONE_ID = "FIELD_ONE_ID"
        private const val FIELD_TWO_ID = "FIELD_TWO_ID"
        private const val FIELD_THREE_ID = "FIELD_THREE_ID"
    }

}