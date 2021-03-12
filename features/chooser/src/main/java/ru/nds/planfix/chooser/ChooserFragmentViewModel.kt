package ru.nds.planfix.chooser

import ru.nds.planfix.base.BaseViewModel

interface ChooserFragmentViewModel: BaseViewModel {
    fun openProductsScan()
    fun openStagesScan()
}