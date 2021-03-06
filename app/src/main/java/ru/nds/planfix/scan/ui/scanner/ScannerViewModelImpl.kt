package ru.nds.planfix.scan.ui.scanner

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.zxing.Result
import ru.nds.planfix.scan.ui.notifications.NotificationsManager

class ScannerViewModelImpl(
    private val scannerCoordinator: ScannerCoordinator,
    private val notificationsManager: NotificationsManager
) : ViewModel(), ScannerViewModel {

    companion object {
        const val CODE_SCANNED_RESULT = 123
    }

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