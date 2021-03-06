package ru.nds.planfix.scan.ui.scanner

import com.google.zxing.Result

interface ScannerViewModel {
    fun handleResult(rawResult: Result?)
}