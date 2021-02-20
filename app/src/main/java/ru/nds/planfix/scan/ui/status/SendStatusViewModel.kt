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
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.HandbookRecord
import ru.nds.planfix.scan.models.parseHandbookRecords

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
        val requestBody = RequestBody.create("text/plain".toMediaType(), formattedBody)
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
                        );
                    }
                )
        )
    }

    fun sendStatus(position: Int) {
        val formattedBody = PlanFixRequestTemplates.XML_SEND_STAGE_TEMPLATE
        val requestBody = RequestBody.create("text/plain".toMediaType(), formattedBody)
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
                        );
                    }
                )
        )
    }

    override fun onCleared() {
        requests.dispose()
        super.onCleared()
    }
}