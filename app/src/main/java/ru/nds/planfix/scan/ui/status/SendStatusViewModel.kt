package ru.nds.planfix.scan.ui.status

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.BehaviorSubject
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.HandbookRecord
import ru.nds.planfix.scan.models.parseHandbookRecords
import java.text.SimpleDateFormat
import java.util.*

class SendStatusViewModel : ViewModel() {

    private val requests = CompositeDisposable()

    private var stagesPrefs: IPrefsStorage? = null

    val actionSuccessSubject = PublishSubject.create<Unit>()
    val stagesSubject = BehaviorSubject.create<List<HandbookRecord>>()

    fun setPrefs(stagesPrefs: IPrefsStorage) {
        this.stagesPrefs = stagesPrefs
        loadStages()
    }

    private fun loadStages() {
        val formattedBody = PlanFixRequestTemplates.XML_GET_STAGES
        val requestBody = formattedBody.toRequestBody("text/plain".toMediaType())
        val authHeader = "Basic ${stagesPrefs?.authHeader}"
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
                        stagesSubject.onNext(it)
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} loadStages error: ${it.message}"
                        )
                    }
                )
        )
    }

    fun sendStatus(position: Int) {
        val stageKey = stagesSubject.value[position].customData[1].value
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
                        actionSuccessSubject.onNext(Unit)
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} sendStatus error: ${it.message}"
                        )
                    }
                )
        )
    }

    override fun onCleared() {
        requests.dispose()
        super.onCleared()
    }
}