package ru.nds.planfix.selecttask

import android.util.Log
import androidx.lifecycle.MutableLiveData
import ru.nds.planfix.base.BaseScreenState
import ru.nds.planfix.base.BaseViewModelImpl
import ru.nds.planfix.base.FullScreenMessageData
import ru.nds.planfix.notifications.NotificationsManager
import ru.nds.planfix.scan.appResources.AppResources
import ru.nds.planfix.selecttask.domain.SelectTasksRepository
import ru.nds.planfix.selecttask.domain.TaskEntity
import ru.nds.planfix.selecttask.domain.TaskStatusEntity

class SelectTaskViewModelImpl(
    appResources: AppResources,
    notificationsManager: NotificationsManager,
    private val selectTasksRepository: SelectTasksRepository,
    private val selectTaskCoordinator: SelectTaskCoordinator,
) : BaseViewModelImpl(appResources, notificationsManager), SelectTaskViewModel {

    override val tasks = MutableLiveData<List<TaskEntity>>()
    override val statuses = MutableLiveData<List<TaskStatusEntity>>()

    init {
        checkSettings()
    }

    override fun reload() {
        checkSettings()
    }

    private fun checkSettings() {
        screenState.postValue(BaseScreenState.ShowContent)
        if (selectTasksRepository.isSettingsScanned()) {
            loadTasks()
        } else {
            screenState.value = BaseScreenState.FullScreenMessage(
                messageData = FullScreenMessageData(
                    titleRes = R.string.scan_prefs_error_title,
                    messageRes = R.string.scan_prefs_error_message,
                    actionNameRes = R.string.scan_prefs_error_action,
                    action = { checkSettings() }
                )
            )
        }
    }

    private fun loadTasks() {
        disposables.add(
            selectTasksRepository.loadTasksAndStatuses()
                .withProcessing(true) { loadTasks() }
                .subscribe { result ->
                    Log.d(
                        "APP_TAG",
                        "${this::class.java.simpleName} ${this::class.java.hashCode()} result: $result"
                    );
                    tasks.value = result.tasks
                }
        )
    }

    override fun onTaskSelected(position: Int) {
        tasks.value?.getOrNull(position)?.also {
            statuses.value = it.statuses
        }
    }

    override fun onStatusSelected(position: Int) {

    }

    override fun onSaveClick(taskPosition: Int, statusPosition: Int) {
        val selectedTask = tasks.value?.get(taskPosition)
        val selectedStatus = statuses.value?.get(statusPosition)
        if (selectedTask != null && selectedStatus != null) {
            sendStatus(selectedTask, selectedStatus)
        }
    }

    private fun sendStatus(task: TaskEntity, status: TaskStatusEntity) {
        disposables.add(
            selectTasksRepository.sendStatus(
                taskId = task.taskId,
                statusId = status.statusId
            ).withProcessing()
                .subscribe {
                    notificationsManager.showNotification(appResources.getString(R.string.notification_success))
                    reload()
                }
        )
    }
}