package ru.nds.planfix.scan.ui.chooser

import androidx.lifecycle.ViewModel

class ChooserFragmentViewModelImpl(
    private val chooserCoordinator: ChooserCoordinator
) : ViewModel(), ChooserFragmentViewModel {
    override fun openProductsScan() {
        chooserCoordinator.openProductsScan()
    }

    override fun openStagesScan() {
        chooserCoordinator.openStagesScan()
    }
}