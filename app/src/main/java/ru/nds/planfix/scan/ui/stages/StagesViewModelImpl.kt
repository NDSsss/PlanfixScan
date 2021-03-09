package ru.nds.planfix.scan.ui.stages

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.appResources.AppResources
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.HandbookRecord
import ru.nds.planfix.scan.models.parseHandbookRecords
import ru.nds.planfix.scan.ui.chooser.BaseCoordinator
import ru.nds.planfix.scan.ui.notifications.NotificationsManager
import java.text.SimpleDateFormat
import java.util.*

class StagesViewModelImpl(
    private val stagesPrefs: IPrefsStorage,
    private val notificationsManager: NotificationsManager,
    private val appResources: AppResources,
    private val coordinator: BaseCoordinator,
) : ViewModel(), StagesViewModel {

    private val requests = CompositeDisposable()
    override val stages: MutableLiveData<List<HandbookRecord>> = MutableLiveData(listOf())

    private fun loadStages() {
        val formattedBody = PlanFixRequestTemplates.XML_GET_STAGES
        val requestBody = formattedBody.toRequestBody("text/plain".toMediaType())
        val authHeader = "Basic ${stagesPrefs.authHeader}"
        requests.add(
            NetworkObjectsHolder.barcodeParseApi.sendParsingToPlanFix(
                url = PlanFixRequestTemplates.PLANFIX_API_URL,
                authHeader = authHeader,
                body = requestBody
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { it.parseHandbookRecords() }
                .subscribe(
                    {
                        stages.value = it
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} loadStages error: ${it.message}"
                        )
                    }
                )
        )
    }

    override fun sendStatus(position: Int) {
        val stageKey = stages.value?.getOrNull(position)?.customData?.getOrNull(1)?.value
        if(stageKey == null){
            showNotFoundError()
            return
        }
        val format = SimpleDateFormat("dd-MM-yyyy")
        val nnPrefs = stagesPrefs ?: return
        val formattedBody = PlanFixRequestTemplates.XML_SEND_STAGE_TEMPLATE.format(
            nnPrefs.account,
            nnPrefs.sid,
            nnPrefs.userLogin,
            nnPrefs.taskId,
            nnPrefs.analyticId,
            nnPrefs.fieldOneId,
            nnPrefs.contactId,
            nnPrefs.fieldTwoId,
            stageKey,
            nnPrefs.fieldThreeId,
            format.format(Calendar.getInstance().time)
        ).replace("\\n", "")
        val requestBody = formattedBody.toRequestBody("text/plain".toMediaType())
        val authHeader = "Basic ${stagesPrefs?.authHeader}"
        requests.add(
            NetworkObjectsHolder.barcodeParseApi.sendParsingToPlanFix(
                url = PlanFixRequestTemplates.PLANFIX_API_URL,
                authHeader = authHeader,
                body = requestBody
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        notificationsManager.showNotification(appResources.getString(R.string.notification_success))
                        coordinator.back()
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} sendStatus error: ${it.message}"
                        )
                        notificationsManager.showNotification(it.message)
                    }
                )
        )
    }

    private fun showNotFoundError() {
        notificationsManager.showNotification(appResources.getString(R.string.error_stage_not_selected))
    }

    override fun onCleared() {
        requests.dispose()
        super.onCleared()
    }
}