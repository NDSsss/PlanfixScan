package ru.nds.planfix.selecttask.di

import org.koin.dsl.module
import ru.nds.planfix.selecttask.domain.SelectTasksRepository
import ru.nds.planfix.selecttask.domain.SelectTasksRepositoryImpl

internal val selectTasksDomainModule = module {
    factory<SelectTasksRepository> {
        SelectTasksRepositoryImpl(
            planfixApi = get(),
            taskPrefs = get(),
            schedulersProvider = get()
        )
    }
}