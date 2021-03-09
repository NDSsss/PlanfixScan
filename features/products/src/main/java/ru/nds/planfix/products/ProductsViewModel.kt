package ru.nds.planfix.products

import androidx.lifecycle.LiveData
import ru.nds.planfix.models.CodeModel

interface ProductsViewModel {
    val productsList: LiveData<List<ru.nds.planfix.models.CodeModel>>

    fun onCodeScanned(code: String)
    fun sendParsingToPlanFix()
    fun openScanner()
    fun onProductDelete(position: Int)
}