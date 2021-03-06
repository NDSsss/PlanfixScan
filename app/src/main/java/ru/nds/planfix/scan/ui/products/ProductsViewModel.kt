package ru.nds.planfix.scan.ui.products

import androidx.lifecycle.LiveData
import ru.nds.planfix.scan.models.CodeModel

interface ProductsViewModel {
    val productsList: LiveData<List<CodeModel>>

    fun onCodeScanned(code: String)
    fun sendParsingToPlanFix()
    fun openScanner()
    fun onProductDelete(position: Int)
}