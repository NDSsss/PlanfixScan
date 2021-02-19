package ru.nds.planfix.scan.ui.codes

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.nds.planfix.scan.YandexMetricaActions
import ru.nds.planfix.scan.data.BarcodeParseApi
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.models.CodeModel
import ru.nds.planfix.scan.models.SettingsQr
import ru.nds.planfix.scan.models.toCodeModel

class CodesViewModel : ViewModel() {

    val codeParsedSubject: PublishSubject<CodeModel> = PublishSubject.create()

    val actionSuccessSubject = PublishSubject.create<Unit>()
    val actionFailSubject = PublishSubject.create<String>()
    val clearCodesSubject = PublishSubject.create<Unit>()

    var prefs: IPrefsStorage? = null

    private val requests = CompositeDisposable()

    private val gson: Gson = GsonBuilder()
        .setLenient()
        .create()

    private val retrofit: Retrofit = Retrofit.Builder()
        .client(
            OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                })
                .build()
        )
        .baseUrl("https://barcode-list.ru/")
        .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(
            GsonConverterFactory.create(gson)
        )
        .build()

    private val barcodeParseApi: BarcodeParseApi = retrofit.create(BarcodeParseApi::class.java)

    fun onSettingsQrScanned(configJson: String) {
        YandexMetricaActions.onSettingsScanned(configJson)
        val settingsJson = gson.fromJson(configJson, SettingsQr::class.java)
        createAuth(settingsJson)
        getSid(settingsJson)
        prefs?.apply {
            account = settingsJson.account
            userLogin = settingsJson.userLogin
            taskId = settingsJson.taskId
            analyticId = settingsJson.analiticId
            fieldId = settingsJson.analiticFieldId
        }
    }

    private fun createAuth(settingsQr: SettingsQr) {
        prefs?.generateAuth(settingsQr.apiKey, settingsQr.token)
    }

    private fun getSid(settingsQr: SettingsQr) {
        val formattedBody = XML_SID_GENERATE.format(
            settingsQr.account,
            settingsQr.userLogin,
            settingsQr.userPassword
        ).replace("\\n", "")
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} formattedBody: $formattedBody"
        );
        val requestBody = RequestBody.create("text/plain".toMediaType(), formattedBody)
        val authHeader = "Basic ${prefs?.authHeader}"

        requests.add(
            barcodeParseApi.sendParsingToPlanFix(
                url = PLANFIX_API_URL,
                authHeader = authHeader,
                body = requestBody
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response ->
                        response.body()?.toSid()?.let { sid ->
                            prefs?.sid = sid
                        }
                        actionSuccessSubject.onNext(Unit)
                    }, {
                        actionFailSubject.onNext("Ошибка получения sid")
                    }
                )
        )

    }

    fun onCodeScanned(code: String) {
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
                        codeParsedSubject.onNext(parsedCode)
                        YandexMetricaActions.onProductScanned(prefs!!, code, parsedCode.toParsedResult())
                    }, {
                        Log.d(
                            "APP_TAG",
                            "${this::class.java.simpleName} ${this::class.java.hashCode()} parse error : ${it.message}"
                        );
                        YandexMetricaActions.onProductScanned(prefs!!, code,"Ошибка ${it.message}")
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
            nnPrefs.fieldId,
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

    private companion object {
        private const val BARCODE_PARSE_URL =
            "https://barcode-list.ru/barcode/RU/%D0%9F%D0%BE%D0%B8%D1%81%D0%BA.htm"
        private const val PLANFIX_API_URL = "https://api.planfix.ru/xml/"
        private const val XML_REQUEST_TEMPLATE =
            "<request method=\u0022action.add\u0022><account>%1\$s</account><sid>%2\$s</sid><action><description>Запись из Приложения от %3\$s</description><task><id>%4\$s</id></task><analitics><analitic><id>%5\$s</id><analiticData><itemData><fieldId>%6\$s</fieldId><value>%7\$s</value></itemData></analiticData></analitic></analitics></action></request>"
        private const val XML_SID_GENERATE =
            "<request method=\u0022auth.login\u0022><account>%1\$s</account><login>%2\$s</login><password>%3\$s</password></request>"
    }

    private fun ResponseBody?.toSid(): String {
        val rawResponse = this?.byteStream()?.readBytes()?.decodeToString()
        return rawResponse?.toSid() ?: ""
    }

    private fun String.toSid(): String {
        val regex = "<sid>(.*?)</sid>".toRegex()
        return try {
            val sid = regex.find(this)
            sid?.groupValues?.getOrNull(1)
                ?: ""
        } catch (e: Exception) {
            ""
        }
    }
}