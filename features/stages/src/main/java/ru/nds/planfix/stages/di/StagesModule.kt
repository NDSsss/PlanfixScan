package ru.nds.planfix.stages.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.prefs.StagesPrefs
import ru.nds.planfix.stages.StagesViewModelImpl

val stagesModule = module {
    viewModel {
        StagesViewModelImpl(
            stagesPrefs = get<StagesPrefs>(),
            notificationsManager = get(),
            appResources = get(),
            coordinator = get(),
            barcodeParseApi = get(),
        )
    }
}