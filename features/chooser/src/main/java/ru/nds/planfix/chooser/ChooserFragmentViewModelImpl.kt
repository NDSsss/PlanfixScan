package ru.nds.planfix.chooser

import ru.nds.planfix.base.BaseViewModelImpl

class ChooserFragmentViewModelImpl(
    private val chooserCoordinator: ChooserCoordinator
) : BaseViewModelImpl(), ChooserFragmentViewModel {
    override fun openProductsScan() {
        chooserCoordinator.openProductsScan()
    }

    override fun openStagesScan() {
        chooserCoordinator.openStagesScan()
    }
}