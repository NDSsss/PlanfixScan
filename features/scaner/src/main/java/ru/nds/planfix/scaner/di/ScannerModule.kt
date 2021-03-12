package ru.nds.planfix.scaner.di

import org.koin.dsl.module
import ru.nds.planfix.scaner.ScannerViewModelImpl

val scannerModule = module {
    factory {
        ru.nds.planfix.scaner.ScannerViewModelImpl(
            scannerCoordinator = get(),
            notificationsManager = get()
        )
    }
}