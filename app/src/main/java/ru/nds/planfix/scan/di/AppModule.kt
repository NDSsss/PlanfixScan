package ru.nds.planfix.scan.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.data.StagesPrefs
import ru.nds.planfix.scan.ui.main.MainActivityViewModelImpl
import ru.nds.planfix.scan.ui.main.MainCoordinator
import ru.nds.planfix.scan.ui.navigation.GlobalCoordinator
import ru.nds.planfix.coordinator.SetUpCoordinator

val appModule = module {
    viewModel {
        MainActivityViewModelImpl(
            notificationsManagerSetUp = get(),
            notificationsManager = get(),
            setUpCoordinator = get(),
            mainCoordinator = get(),
            productPrefs = get<ProductsPrefs>(),
            stagesPrefs = get<StagesPrefs>(),
            appResources = get(),
            barcodeParseApi = get(),
            gson = get(),
            yametric = get(),
        )
    }

    single { GlobalCoordinator() }

    factory<SetUpCoordinator> { get<GlobalCoordinator>() }
    factory<ru.nds.planfix.coordinator.BaseCoordinator> { get<GlobalCoordinator>() }
    factory<MainCoordinator> { get<GlobalCoordinator>() }
    factory<ru.nds.planfix.chooser.ChooserCoordinator> { get<GlobalCoordinator>() }
    factory<ru.nds.planfix.scaner.ScannerCoordinator> { get<GlobalCoordinator>() }
    factory<ru.nds.planfix.products.ProductsCoordinator> { get<GlobalCoordinator>() }
}