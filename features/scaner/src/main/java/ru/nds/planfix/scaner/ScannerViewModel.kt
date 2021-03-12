package ru.nds.planfix.scaner

import com.google.zxing.Result
import ru.nds.planfix.base.BaseViewModel

interface ScannerViewModel : BaseViewModel {
    fun handleResult(rawResult: Result?)
}