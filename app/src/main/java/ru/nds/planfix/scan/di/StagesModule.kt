package ru.nds.planfix.scan.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.scan.data.StagesPrefs
import ru.nds.planfix.scan.ui.stages.StagesViewModelImpl

val stagesModule = module {
    viewModel {
        StagesViewModelImpl(
            stagesPrefs = get<StagesPrefs>(),
            notificationsManager = get(),
            appResources = get(),
            coordinator = get()
        )
    }
}