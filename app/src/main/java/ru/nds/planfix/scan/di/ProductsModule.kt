package ru.nds.planfix.scan.di

import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.ui.products.ProductsViewModelImpl

val productsModule = module {
    viewModel { ProductsViewModelImpl(
        prefs = get<ProductsPrefs>(),
        appResources = get(),
        notificationsManager = get(),
        productsCoordinator = get()
    ) }
}