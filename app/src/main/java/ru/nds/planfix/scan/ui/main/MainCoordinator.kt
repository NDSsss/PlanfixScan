package ru.nds.planfix.scan.ui.main

import ru.nds.planfix.scan.ui.chooser.BaseCoordinator

interface MainCoordinator : BaseCoordinator {
    fun openChooser()
    fun openScanner()
}