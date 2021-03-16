package ru.nds.planfix.selecttask.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.selecttask.SelectTaskViewModelImpl

internal val selectTaskPresentationModule = module {
    viewModel {
        SelectTaskViewModelImpl(
            appResources = get(),
            notificationsManager = get(),
            selectTasksRepository = get(),
            selectTaskCoordinator = get(),
        )
    }
}