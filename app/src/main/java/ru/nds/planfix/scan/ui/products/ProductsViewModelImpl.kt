package ru.nds.planfix.scan.ui.products

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.YandexMetricaActions
import ru.nds.planfix.scan.appResources.AppResources
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.BARCODE_PARSE_URL
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.PLANFIX_API_URL
import ru.nds.planfix.scan.data.PlanFixRequestTemplates.XML_REQUEST_TEMPLATE
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.CodeModel
import ru.nds.planfix.scan.models.toCodeModel
import ru.nds.planfix.scan.ui.notifications.NotificationsManager
import ru.nds.planfix.scan.ui.scanner.ScannerViewModelImpl

class ProductsViewModelImpl(
    val prefs: IPrefsStorage,
    private val productsCoordinator: ProductsCoordinator,
    private val appResources: AppResources,
    private val notificationsManager: NotificationsManager,
) : ViewModel(), ProductsViewModel {

    private val requests = CompositeDisposable()

    override val productsList = MutableLiveData<List<CodeModel>>(listOf())

    override fun onCodeScanned(code: String) {
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
                        onCodeParsed(parsedCode)
                        YandexMetricaActions.onProductScanned(
                            prefs,
                            code,
                            parsedCode.toParsedResult()
                        )
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} parse error : ${it.message}"
                        )
                        YandexMetricaActions.onProductScanned(prefs, code, "Ошибка ${it.message}")
                        notificationsManager.showNotification(appResources.getString(R.string.error_barcode_parsing))
                    }
                )
        )
    }

    override fun sendParsingToPlanFix() {
        val codes: List<CodeModel> = listOf()
        val parsingResult = "[${
            codes.joinToString(
                separator = "\u0022,\u0022",
                prefix = "\u0022",
                postfix = "\u0022"
            ) { it.toParsedResult() }
        }]"
        val nnPrefs = prefs
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} parsingResult: $parsingResult"
        )
        val formattedBody = XML_REQUEST_TEMPLATE.format(
            nnPrefs.account,
            nnPrefs.sid,
            nnPrefs.userLogin,
            nnPrefs.taskId,
            nnPrefs.analyticId,
            nnPrefs.fieldOneId,
            parsingResult
        ).replace("\\n", "")

        YandexMetricaActions.onProductsSent(prefs, formattedBody)
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} formattedBody: $formattedBody"
        )
        val requestBody = formattedBody.toRequestBody("text/plain".toMediaType())
        val authHeader = "Basic ${prefs.authHeader}"
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
                        )
                        notificationsManager.showNotification(appResources.getString(R.string.notification_success))
                        clearCodes()
                        productsCoordinator.back()
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} sendParsingToPlanFix: error"
                        )
                        notificationsManager.showNotification(appResources.getString(R.string.error_send_data_to_planfix))
                    }
                )
        )
    }

    private fun clearCodes() {
        productsList.value = listOf()
    }

    private fun onCodeParsed(product: CodeModel) {
        val currentProductsList =
            (productsList.value ?: throw NullPointerException("codes must be initialized"))
        productsList.value = currentProductsList.plus(product)
    }

    override fun openScanner() {
        requests.add(
            productsCoordinator.addResultListener<String>(ScannerViewModelImpl.CODE_SCANNED_RESULT)
                .subscribe { onCodeScanned(it) }
        )
        productsCoordinator.openScanner()
    }

    override fun onProductDelete(position: Int) {
        val currentProductsList =
            (productsList.value ?: throw NullPointerException("codes must be initialized"))
        productsList.value = currentProductsList.filterIndexed { index, _ -> index != position }
    }

    override fun onCleared() {
        requests.dispose()
        super.onCleared()
    }
}