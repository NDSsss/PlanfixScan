package ru.nds.planfix.scan

import android.app.Application
import com.yandex.metrica.YandexMetrica

import com.yandex.metrica.YandexMetricaConfig
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.loadKoinModules
import org.koin.core.context.startKoin
import ru.nds.planfix.scan.di.*


class App : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin()
        val config =
            YandexMetricaConfig.newConfigBuilder("4b3840a5-66e1-4b1c-b7e8-85ceb0e39ee1").build()
        // Initializing the AppMetrica SDK.
        // Initializing the AppMetrica SDK.
        YandexMetrica.activate(applicationContext, config)
        // Automatic tracking of user activity.
        // Automatic tracking of user activity.
        YandexMetrica.enableActivityAutoTracking(this)
    }

    private fun initKoin() {
        startKoin {
            androidContext(this@App)
            modules(appModule)
        }
        loadKoinModules(appResourcesModule)
        loadKoinModules(prefsModule)
        loadKoinModules(notificationsModule)
        loadKoinModules(chooserModule)
        loadKoinModules(productsModule)
        loadKoinModules(scannerModule)
    }
}