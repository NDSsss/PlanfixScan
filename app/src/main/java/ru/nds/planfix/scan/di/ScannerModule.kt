package ru.nds.planfix.scan.di

import org.koin.dsl.module
import ru.nds.planfix.scan.ui.scanner.ScannerViewModelImpl

val scannerModule = module {
    factory {
        ScannerViewModelImpl(
            scannerCoordinator = get(),
            notificationsManager = get()
        )
    }
}