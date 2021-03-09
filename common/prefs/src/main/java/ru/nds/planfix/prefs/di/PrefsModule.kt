package ru.nds.planfix.prefs.di

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