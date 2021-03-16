package ru.nds.planfix.chooser

import ru.nds.planfix.base.BaseViewModelImpl
import ru.nds.planfix.notifications.NotificationsManager
import ru.nds.planfix.scan.appResources.AppResources

class ChooserFragmentViewModelImpl(
    private val chooserCoordinator: ChooserCoordinator,
    appResources: AppResources,
    notificationsManager: NotificationsManager
) : BaseViewModelImpl(appResources, notificationsManager), ChooserFragmentViewModel {
    override fun openProductsScan() {
        chooserCoordinator.openProductsScan()
    }

    override fun openStagesScan() {
        chooserCoordinator.openStagesScan()
    }
}