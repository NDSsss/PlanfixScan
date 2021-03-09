package ru.nds.planfix.products

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import ru.nds.planfix.models.toCodeModel
import ru.nds.planfix.network.BarcodeParseApi
import ru.nds.planfix.network.PlanFixRequestTemplates.BARCODE_PARSE_URL
import ru.nds.planfix.network.PlanFixRequestTemplates.PLANFIX_API_URL
import ru.nds.planfix.network.PlanFixRequestTemplates.XML_REQUEST_TEMPLATE
import ru.nds.planfix.notifications.NotificationsManager
import ru.nds.planfix.resultcodes.CODE_SCANNED_RESULT
import ru.nds.planfix.scan.appResources.AppResources
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.yametric.IYandexMetricaActions

class ProductsViewModelImpl(
    val prefs: IPrefsStorage,
    private val productsCoordinator: ProductsCoordinator,
    private val appResources: AppResources,
    private val notificationsManager: NotificationsManager,
    private val barcodeParseApi: BarcodeParseApi,
    private val yametric: IYandexMetricaActions,
) : ViewModel(), ProductsViewModel {

    private val requests = CompositeDisposable()

    override val productsList = MutableLiveData<List<ru.nds.planfix.models.CodeModel>>(listOf())

    override fun onCodeScanned(code: String) {
        requests.add(
            barcodeParseApi.parseBarcode(
                BARCODE_PARSE_URL,
                code
            )
                .map { it.toCodeModel(code) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { parsedCode ->
                        onCodeParsed(parsedCode)
                        yametric.onProductScanned(
                            code,
                            parsedCode.toParsedResult()
                        )
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} parse error : ${it.message}"
                        )
                        yametric.onProductScanned(code, "Ошибка ${it.message}")
                        notificationsManager.showNotification(appResources.getString(R.string.error_barcode_parsing))
                    }
                )
        )
    }

    override fun sendParsingToPlanFix() {
        val codes: List<ru.nds.planfix.models.CodeModel> = listOf()
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

        yametric.onProductsSent(formattedBody)
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} formattedBody: $formattedBody"
        )
        val requestBody = formattedBody.toRequestBody("text/plain".toMediaType())
        val authHeader = "Basic ${prefs.authHeader}"
        requests.add(
            barcodeParseApi.sendParsingToPlanFix(
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

    private fun onCodeParsed(product: ru.nds.planfix.models.CodeModel) {
        val currentProductsList =
            (productsList.value ?: throw NullPointerException("codes must be initialized"))
        productsList.value = currentProductsList.plus(product)
    }

    override fun openScanner() {
        requests.add(
            productsCoordinator.addResultListener<String>(CODE_SCANNED_RESULT)
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