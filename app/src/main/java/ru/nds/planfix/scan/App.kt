package ru.nds.planfix.scan

import android.app.Application
import com.yandex.metrica.YandexMetrica

import com.yandex.metrica.YandexMetricaConfig


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        val config =
            YandexMetricaConfig.newConfigBuilder("4b3840a5-66e1-4b1c-b7e8-85ceb0e39ee1").build()
        // Initializing the AppMetrica SDK.
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)
    }
}