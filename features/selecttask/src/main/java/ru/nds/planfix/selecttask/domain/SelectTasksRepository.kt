package ru.nds.planfix.selecttask.domain

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

interface SelectTasksRepository {
    fun isSettingsScanned(): Boolean
    fun loadTasksAndStatuses(): Single<TaskEntityResponse>
    fun sendStatus(taskId: String,statusId: String): Completable
}