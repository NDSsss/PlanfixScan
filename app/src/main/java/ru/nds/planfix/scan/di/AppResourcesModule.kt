package ru.nds.planfix.scan.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nds.planfix.scan.appResources.AndroidAppResources
import ru.nds.planfix.scan.appResources.AppResources

val appResourcesModule = module {
    factory<AppResources> { AndroidAppResources(androidContext().resources) }
}