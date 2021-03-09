package ru.nds.planfix.chooser

import ru.nds.planfix.coordinator.BaseCoordinator

interface ChooserCoordinator : BaseCoordinator {
    fun openProductsScan()
    fun openStagesScan()
}