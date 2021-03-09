package ru.nds.planfix.yametric

interface IYandexMetricaActions {
    fun onSettingsScanned(settingsJson: String)
    fun onProductScanned(productBarCode: String, title: String)
    fun onProductsSent(requset: String)
    fun onCompanyExpired(company: String)

}