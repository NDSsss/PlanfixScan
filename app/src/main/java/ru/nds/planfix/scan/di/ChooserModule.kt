package ru.nds.planfix.scan.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.scan.ui.chooser.ChooserFragmentViewModelImpl

val chooserModule = module {
    viewModel {
        ChooserFragmentViewModelImpl(
            chooserCoordinator = get()
        )
    }
}