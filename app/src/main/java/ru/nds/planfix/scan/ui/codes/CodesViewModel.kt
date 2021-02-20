package ru.nds.planfix.scan.ui.codes

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import ru.nds.planfix.scan.YandexMetricaActions
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.BARCODE_PARSE_URL
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.PLANFIX_API_URL
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.XML_REQUEST_TEMPLATE
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.CodeModel
import ru.nds.planfix.scan.models.toCodeModel

class CodesViewModel : ViewModel() {

    val codeParsedSubject: PublishSubject<CodeModel> = PublishSubject.create()

    val actionSuccessSubject = PublishSubject.create<Unit>()
    val actionFailSubject = PublishSubject.create<String>()
    val clearCodesSubject = PublishSubject.create<Unit>()

    var prefs: IPrefsStorage? = null

    private val requests = CompositeDisposable()

    fun onCodeScanned(code: String) {
        requests.add(
            NetworkObjectsHolder.barcodeParseApi.parseBarcode(
                BARCODE_PARSE_URL,
                code
            )
                .map { it.toCodeModel(code) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { parsedCode ->
                        codeParsedSubject.onNext(parsedCode)
                        YandexMetricaActions.onProductScanned(
                            prefs!!,
                            code,
                            parsedCode.toParsedResult()
                        )
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} parse error : ${it.message}"
                        );
                        YandexMetricaActions.onProductScanned(prefs!!, code, "Ошибка ${it.message}")
                        actionFailSubject.onNext("Ошибка парсинга штрих-кода")
                    }
                )
        )
    }

    fun sendParsingToPlanFix(codes: MutableList<CodeModel>) {
        val parsingResult = "[${
            codes.joinToString(
                separator = "\u0022,\u0022",
                prefix = "\u0022",
                postfix = "\u0022"
            ) { it.toParsedResult() }
        }]"
        val nnPrefs = prefs ?: return
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} parsingResult: $parsingResult"
        );
        val formattedBody = XML_REQUEST_TEMPLATE.format(
            nnPrefs.account,
            nnPrefs.sid,
            nnPrefs.userLogin,
            nnPrefs.taskId,
            nnPrefs.analyticId,
            nnPrefs.fieldOneId,
            parsingResult
        ).replace("\\n", "")

        YandexMetricaActions.onProductsSent(prefs!!, formattedBody)
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} formattedBody: $formattedBody"
        );
        val requestBody = RequestBody.create("text/plain".toMediaType(), formattedBody)
        val authHeader = "Basic ${prefs?.authHeader}"
        requests.add(
            NetworkObjectsHolder.barcodeParseApi.sendParsingToPlanFix(
                url = PLANFIX_API_URL,
                authHeader = authHeader,
                body = requestBody
            )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} sendParsingToPlanFix: success"
                        );
                        actionSuccessSubject.onNext(Unit)
                        clearCodesSubject.onNext(Unit)
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} sendParsingToPlanFix: error"
                        );
                        actionFailSubject.onNext("Ошибка отправки данных в Planfix")
                    }
                )
        )
    }

    override fun onCleared() {
        requests.dispose()
        super.onCleared()
    }
}