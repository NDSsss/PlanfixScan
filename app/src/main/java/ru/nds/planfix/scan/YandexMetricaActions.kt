package ru.nds.planfix.scan

import com.yandex.metrica.YandexMetrica
import ru.nds.planfix.scan.data.IPrefsStorage

interface IYandexMetricaActions {
    fun onSettingsScanned(settingsJson: String)
    fun onProductScanned(prefs: IPrefsStorage, productBarCode: String, title: String)
    fun onProductsSent(prefs: IPrefsStorage, requset: String)
    fun onCompanyExpired(company: String)

}

object YandexMetricaActions : IYandexMetricaActions {
    override fun onSettingsScanned(settingsJson: String) {
        YandexMetrica.reportEvent("SETTINGS SCANNED", settingsJson)
    }

    override fun onProductScanned(prefs: IPrefsStorage, productBarCode: String, title: String) {
        YandexMetrica.reportEvent("PRODUCT SCANNED", "{\"barCode\":\"$productBarCode\"")
    }

    override fun onProductsSent(prefs: IPrefsStorage, requset: String) {
        YandexMetrica.reportEvent("PRODUCTS SENT", "{\"request\":\"$requset\"")
    }

    override fun onCompanyExpired(company: String) {
        YandexMetrica.reportEvent("COMPANY EXPIRED", "{\"company\":\"$company\"")
    }
}