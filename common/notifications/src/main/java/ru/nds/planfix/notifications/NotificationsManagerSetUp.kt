package ru.nds.planfix.notifications

import android.app.Activity

interface NotificationsManagerSetUp {
    fun setActivity(activity: Activity)
    fun removeActivity()
}