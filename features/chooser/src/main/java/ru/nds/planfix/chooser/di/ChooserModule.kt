package ru.nds.planfix.chooser.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.chooser.ChooserFragmentViewModelImpl

val chooserModule = module {
    viewModel {
        ChooserFragmentViewModelImpl(
            appResources = get(),
            notificationsManager = get(),
            chooserCoordinator = get()
        )
    }
}