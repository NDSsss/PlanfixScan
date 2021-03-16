package ru.nds.planfix.prefs.di

import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module
import ru.nds.planfix.prefs.TasksPrefsStorage
import ru.nds.planfix.prefs.ITasksPrefs
import ru.nds.planfix.prefs.ProductsPrefs
import ru.nds.planfix.prefs.StagesPrefs

val prefsModule = module {
    factory {
        ProductsPrefs(androidContext())
    }
    factory {
        StagesPrefs(androidContext())
    }
    factory<ITasksPrefs> {
        TasksPrefsStorage(androidContext())
    }
}