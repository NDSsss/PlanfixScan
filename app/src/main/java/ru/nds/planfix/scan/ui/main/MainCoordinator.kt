package ru.nds.planfix.scan.ui.main

import ru.nds.planfix.coordinator.BaseCoordinator

interface MainCoordinator : ru.nds.planfix.coordinator.BaseCoordinator {
    fun openTasks()
    fun openChooser()
    fun openScanner()
}