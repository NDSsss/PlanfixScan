package ru.nds.planfix.selecttask.domain

import android.util.Log
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import ru.nds.planfix.network.PlanFixRequestTemplates
import ru.nds.planfix.network.PlanfixApi
import ru.nds.planfix.network.SchedulersProvider
import ru.nds.planfix.network.subToThreads
import ru.nds.planfix.prefs.ITasksPrefs

class SelectTasksRepositoryImpl(
    private val planfixApi: PlanfixApi,
    private val taskPrefs: ITasksPrefs,
    private val schedulersProvider: SchedulersProvider,
) : SelectTasksRepository {

    override fun isSettingsScanned(): Boolean {
        return taskPrefs.account.isNotBlank()
                && taskPrefs.authHeader.isNotBlank()
                && taskPrefs.generalTaskNumber.isNotBlank()
    }

    override fun loadTasksAndStatuses(): Single<TaskEntityResponse> {
        return planfixApi.sendParsingToPlanFix(
            url = PlanFixRequestTemplates.PLANFIX_API_URL,
            authHeader = taskPrefs.authHeader,
            body = PlanFixRequestTemplates.createGetTasksAndStatusesKeysRequest(
                account = taskPrefs.account,
                taskNumber = taskPrefs.generalTaskNumber
            )
        ).subToThreads(schedulersProvider)
            .map { it.toTaskActionsKeys() }
            .flatMap { keys ->
                Log.d(
                    "APP_TAG",
                    "${this::class.java.simpleName} ${this::class.java.hashCode()} keys: $keys"
                );
                loadAnaliticItemsData(keys)
            }
    }

    private fun loadAnaliticItemsData(keys: List<String>): Single<TaskEntityResponse> {
        return planfixApi.sendParsingToPlanFix(
            url = PlanFixRequestTemplates.PLANFIX_API_URL,
            authHeader = taskPrefs.authHeader,
            body = PlanFixRequestTemplates.createStatuscreateGetTasksAndStatusesDataRequest(
                account = taskPrefs.account,
                keys = keys
            )
        ).subToThreads(schedulersProvider)
            .map { it.toTasksEntityResponse(taskPrefs.robotName) }
    }

    override fun sendStatus(taskId: String, statusId: String): Completable {
        return Completable.fromSingle(
            planfixApi.sendParsingToPlanFix(
                url = PlanFixRequestTemplates.PLANFIX_API_URL,
                authHeader = taskPrefs.authHeader,
                body = PlanFixRequestTemplates.createSetStatusRequest(
                    account = taskPrefs.account,
                    description = "Запись из приложения от ${taskPrefs.robotName}",
                    taskId = taskId,
                    analiticId = taskPrefs.analiticId,
                    analiticFieldId = taskPrefs.analiticFieldId,
                    statusId = statusId
                )
            )
        ).subToThreads(schedulersProvider)
    }
}