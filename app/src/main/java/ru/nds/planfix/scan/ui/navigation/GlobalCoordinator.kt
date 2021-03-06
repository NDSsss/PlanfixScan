package ru.nds.planfix.scan.ui.navigation

import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import io.reactivex.rxjava3.core.Observable
import ru.nds.planfix.scan.R
import ru.nds.planfix.coordinator.SetUpCoordinator
import ru.nds.planfix.scan.ui.main.MainCoordinator
import ru.nds.planfix.stages.StagesFragment

class GlobalCoordinator : SetUpCoordinator, ru.nds.planfix.chooser.ChooserCoordinator, MainCoordinator,
    ru.nds.planfix.scaner.ScannerCoordinator,
    ru.nds.planfix.products.ProductsCoordinator {

    private var fm: FragmentManager? = null

    private val pendingOperations: MutableList<(FragmentManager) -> Unit> = mutableListOf()
    private val resultListeners = SparseArray<ResultListener>()

    override fun setFragmentManager(fm: FragmentManager) {
        this.fm = fm
        pendingOperations.forEach {
            it.invoke(fm)
        }
        pendingOperations.clear()
    }

    override fun removeFragmentManager() {
        fm = null
    }

    override fun back() {
        commitOnActiveFm { it.popBackStackImmediate() }
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> addResultListener(code: Int): Observable<T> {
        val res: Observable<T> = Observable.create { subscriber ->
            resultListeners.put(
                code,
                object : ResultListener {
                    override fun onResult(resultData: Any?) {
                        (resultData as? T)?.let {
                            subscriber.onNext(it)
                        }
                    }
                }
            )
        }
        res.doOnDispose {
            removeResultListener(code)
        }
        return res
    }

    override fun removeResultListener(code: Int) {
        resultListeners.remove(code)
    }

    override fun sendResult(code: Int, result: Any?): Boolean {
        val resultListener = resultListeners[code]
        return if (resultListener != null) {
            resultListener.onResult(result)
            true
        } else {
            false
        }
    }

    override fun openChooser() {
        commitOnActiveFm {
            it.beginTransaction()
                .replace(R.id.container, ru.nds.planfix.chooser.ChooserFragment.newInstance(), ru.nds.planfix.products.ProductsFragment.TAG)
                .commitNow()
        }
    }

    override fun openProductsScan() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack(ru.nds.planfix.chooser.ChooserFragment.TAG)
                .replace(R.id.container, ru.nds.planfix.products.ProductsFragment.newInstance(), ru.nds.planfix.products.ProductsFragment.TAG)
                .commit()
        }
    }

    override fun openStagesScan() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack(ru.nds.planfix.chooser.ChooserFragment.TAG)
                .replace(R.id.container, StagesFragment.newInstance(), StagesFragment.TAG)
                .commit()
        }
    }

    override fun openScanner() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack("openScanner")
                .replace(R.id.container, ru.nds.planfix.scaner.ScannerFragment.newInstance(), ru.nds.planfix.scaner.ScannerFragment.TAG)
                .commit()
        }
    }


    private fun commitOnActiveFm(operation: (FragmentManager) -> Unit) {
        fm.apply {
            if (this != null) {
                operation.invoke(this)
            } else {
                pendingOperations.add(operation)
            }
        }
    }
}

interface ResultListener {
    fun onResult(resultData: Any?)
}