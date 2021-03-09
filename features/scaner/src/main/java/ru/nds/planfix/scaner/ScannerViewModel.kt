package ru.nds.planfix.scaner

import com.google.zxing.Result

interface ScannerViewModel {
    fun handleResult(rawResult: Result?)
}