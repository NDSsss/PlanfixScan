package ru.nds.planfix.products

import ru.nds.planfix.coordinator.BaseCoordinator

interface ProductsCoordinator : ru.nds.planfix.coordinator.BaseCoordinator {
    fun openScanner()
}