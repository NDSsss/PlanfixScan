package ru.nds.planfix.scan.di

import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.data.StagesPrefs

val prefsModule = module {
    factory {
        ProductsPrefs(androidContext())
    }
    factory {
        StagesPrefs(androidContext())
    }
}

val PRODUCTS_SETTINGS_PREFS = named("PRODUCTS_SETTINGS_PREFS")
val STAGES_SETTINGS_PREFS = named("STAGES_SETTINGS_PREFS")