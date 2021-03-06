package ru.nds.planfix.scan.ui.notifications

import android.app.Activity

interface NotificationsManagerSetUp {
    fun setActivity(activity: Activity)
    fun removeActivity()
}