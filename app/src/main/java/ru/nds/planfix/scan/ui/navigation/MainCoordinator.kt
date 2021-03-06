package ru.nds.planfix.scan.ui.navigation

import ru.nds.planfix.scan.ui.chooser.BaseCoordinator

interface MainCoordinator : BaseCoordinator {
    fun openChooser()
}