package ru.nds.planfix.scan.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.data.StagesPrefs
import ru.nds.planfix.scan.ui.chooser.ChooserCoordinator
import ru.nds.planfix.scan.ui.main.MainActivityViewModelImpl
import ru.nds.planfix.scan.ui.navigation.GlobalCoordinator
import ru.nds.planfix.scan.ui.navigation.MainCoordinator
import ru.nds.planfix.scan.ui.navigation.SetUpCoordinator
import ru.nds.planfix.scan.ui.products.ProductsCoordinator
import ru.nds.planfix.scan.ui.scanner.ScannerCoordinator

val appModule = module {
    viewModel {
        MainActivityViewModelImpl(
            notificationsManagerSetUp = get(),
            notificationsManager = get(),
            setUpCoordinator = get(),
            mainCoordinator = get(),
            productPrefs = get<ProductsPrefs>(),
            stagesPrefs = get<StagesPrefs>(),
            appResources = get()
        )
    }

    single { GlobalCoordinator() }

    factory<SetUpCoordinator> { get<GlobalCoordinator>() }
    factory<MainCoordinator> { get<GlobalCoordinator>() }
    factory<ChooserCoordinator> { get<GlobalCoordinator>() }
    factory<ScannerCoordinator> { get<GlobalCoordinator>() }
    factory<ProductsCoordinator> { get<GlobalCoordinator>() }
}