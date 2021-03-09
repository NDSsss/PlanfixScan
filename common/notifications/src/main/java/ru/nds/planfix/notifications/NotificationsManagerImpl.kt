package ru.nds.planfix.notifications

import android.app.Activity
import android.widget.Toast

class NotificationsManagerImpl : NotificationsManagerSetUp, NotificationsManager {

    private var activity: Activity? = null
    private val pendingOperations: MutableList<(Activity) -> Unit> = mutableListOf()

    override fun showNotification(message: String?) {
        commitOnActiveActivity {
            Toast.makeText(it, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun setActivity(activity: Activity) {
        this.activity = activity
        pendingOperations.forEach {
            it.invoke(activity)
        }
        pendingOperations.clear()
    }

    override fun removeActivity() {
        activity = null
    }


    private fun commitOnActiveActivity(operation: (Activity) -> Unit) {
        activity.apply {
            if (this != null) {
                operation.invoke(this)
            } else {
                pendingOperations.add(operation)
            }
        }
    }
}