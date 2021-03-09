package ru.nds.planfix.yametric

import com.yandex.metrica.YandexMetrica

internal class YandexMetricaActionsImpl : IYandexMetricaActions {
    override fun onSettingsScanned(settingsJson: String) {
        YandexMetrica.reportEvent("SETTINGS SCANNED", settingsJson)
    }

    override fun onProductScanned(productBarCode: String, title: String) {
        YandexMetrica.reportEvent("PRODUCT SCANNED", "{\"barCode\":\"$productBarCode\"")
    }

    override fun onProductsSent(requset: String) {
        YandexMetrica.reportEvent("PRODUCTS SENT", "{\"request\":\"$requset\"")
    }

    override fun onCompanyExpired(company: String) {
        YandexMetrica.reportEvent("COMPANY EXPIRED", "{\"company\":\"$company\"")
    }
}