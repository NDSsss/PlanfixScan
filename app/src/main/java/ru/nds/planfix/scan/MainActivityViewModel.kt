package ru.nds.planfix.scan

import android.util.Log
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import ru.nds.planfix.scan.data.IPrefsStorage
import ru.nds.planfix.scan.data.PlanFixRequestTemplates
import ru.nds.planfix.scan.data.SidResponse
import ru.nds.planfix.scan.di.NetworkObjectsHolder
import ru.nds.planfix.scan.models.ProductSettingsQr
import ru.nds.planfix.scan.models.StagesSettingsQr

class MainActivityViewModel : ViewModel() {

    var productPrefs: IPrefsStorage? = null
    var stagesPrefs: IPrefsStorage? = null

    private val requests = CompositeDisposable()

    val actionSuccessSubject = PublishSubject.create<Unit>()
    val actionFailSubject = PublishSubject.create<String>()

    fun onProductSettingsQrScanned(configJson: String) {
        YandexMetricaActions.onSettingsScanned(configJson)
        val settingsJson =
            NetworkObjectsHolder.gson.fromJson(configJson, ProductSettingsQr::class.java)
        createAuth(
            apiKey = settingsJson.apiKey,
            token = settingsJson.token,
            settingType = SettingType.PRODUCT
        )
        getSid(
            account = settingsJson.account,
            userLogin = settingsJson.userLogin,
            userPassword = settingsJson.userPassword,
            settingType = SettingType.PRODUCT
        )
        productPrefs?.apply {
            account = settingsJson.account
            userLogin = settingsJson.userLogin
            taskId = settingsJson.taskId
            analyticId = settingsJson.analiticId
            fieldOneId = settingsJson.analiticFieldId
        }
    }

    fun onStagesSettingQrScanned(configJson: String) {
        YandexMetricaActions.onSettingsScanned(configJson)
        val settingsJson =
            NetworkObjectsHolder.gson.fromJson(configJson, StagesSettingsQr::class.java)
        createAuth(
            apiKey = settingsJson.apiKey,
            token = settingsJson.token,
            settingType = SettingType.STAGES
        )
        getSid(
            account = settingsJson.account,
            userLogin = settingsJson.userLogin,
            userPassword = settingsJson.userPassword,
            settingType = SettingType.STAGES
        )
        productPrefs?.apply {
            account = settingsJson.account
            userLogin = settingsJson.userLogin
            taskId = settingsJson.taskId
            contactId = settingsJson.contactId
            analyticId = settingsJson.analiticId
            fieldOneId = settingsJson.contactIdFieldId
            fieldTwoId = settingsJson.stageTypeFieldId
            fieldThreeId = settingsJson.dateFieldId
        }
    }

    private fun createAuth(apiKey: String, token: String, settingType: SettingType) {
        when (settingType) {
            SettingType.PRODUCT -> productPrefs
            SettingType.STAGES -> stagesPrefs
        }?.generateAuth(apiKey, token)
    }

    private fun getSid(
        account: String,
        userLogin: String,
        userPassword: String,
        settingType: SettingType
    ) {
        val formattedBody = PlanFixRequestTemplates.XML_SID_GENERATE.format(
            account,
            userLogin,
            userPassword
        ).replace("\\n", "")
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} formattedBody: $formattedBody"
        );
        val requestBody = RequestBody.create("text/plain".toMediaType(), formattedBody)
        val authHeader = "Basic ${productPrefs?.authHeader}"

        requests.add(
            NetworkObjectsHolder.barcodeParseApi.sendParsingToPlanFix(
                url = PlanFixRequestTemplates.PLANFIX_API_URL,
                authHeader = authHeader,
                body = requestBody
            ).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map { SidResponse.createFromRawResponse(it) }
                .subscribe(
                    { sid ->
                        when (settingType) {
                            SettingType.PRODUCT -> productPrefs
                            SettingType.STAGES -> stagesPrefs
                        }?.sid = sid.sid
                        actionSuccessSubject.onNext(Unit)
                    }, {
                        actionFailSubject.onNext("Ошибка получения sid")
                    }
                )
        )
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

    private enum class SettingType {
        PRODUCT,
        STAGES
    }
}