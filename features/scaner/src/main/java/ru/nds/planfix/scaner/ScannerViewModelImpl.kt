package ru.nds.planfix.scaner

import android.util.Log
import androidx.lifecycle.ViewModel
import ru.nds.planfix.notifications.NotificationsManager
import com.google.zxing.Result
import ru.nds.planfix.resultcodes.CODE_SCANNED_RESULT

class ScannerViewModelImpl(
    private val scannerCoordinator: ScannerCoordinator,
    private val notificationsManager: NotificationsManager
) : ViewModel(), ScannerViewModel {

    override fun handleResult(rawResult: Result?) {
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} rawResult: $rawResult"
        )
        notificationsManager.showNotification(rawResult.toString())
        scannerCoordinator.sendResult(CODE_SCANNED_RESULT, rawResult.toString())
        scannerCoordinator.back()
    }
}