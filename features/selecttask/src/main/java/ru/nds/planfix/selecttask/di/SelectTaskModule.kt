package ru.nds.planfix.selecttask.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.selecttask.SelectTaskViewModelImpl

val selectTaskModule = module {
    viewModel { SelectTaskViewModelImpl() }
}