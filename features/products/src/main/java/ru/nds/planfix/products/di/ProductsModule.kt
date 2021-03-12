package ru.nds.planfix.products.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.products.ProductsViewModelImpl
import ru.nds.planfix.prefs.ProductsPrefs

val productsModule = module {
    viewModel {
        ProductsViewModelImpl(
            prefs = get<ProductsPrefs>(),
            appResources = get(),
            notificationsManager = get(),
            productsCoordinator = get(),
            barcodeParseApi = get(),
            yametric = get()
        )
    }
}