package ru.nds.planfix.scan.ui.navigation

import android.util.Log
import android.util.SparseArray
import androidx.fragment.app.FragmentManager
import io.reactivex.rxjava3.core.Observable
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.ui.chooser.ChooserCoordinator
import ru.nds.planfix.scan.ui.chooser.ChooserFragment
import ru.nds.planfix.scan.ui.products.ProductsCoordinator
import ru.nds.planfix.scan.ui.products.ProductsFragment
import ru.nds.planfix.scan.ui.scanner.ScannerCoordinator
import ru.nds.planfix.scan.ui.scanner.ScannerFragment
import ru.nds.planfix.scan.ui.status.SendStatusFragment

class GlobalCoordinator : SetUpCoordinator, ChooserCoordinator, MainCoordinator, ScannerCoordinator, ProductsCoordinator {

    init {
        Log.d("APP_TAG", "${this::class.java.simpleName} ${this::class.java.hashCode()} : ")
    }

    private var fm: FragmentManager? = null

    private val pendingOperations: MutableList<(FragmentManager) -> Unit> = mutableListOf()
    private val resultListeners = SparseArray<ResultListener>()

    override fun setFragmentManager(fm: FragmentManager) {
        Log.d(
            "APP_TAG",
            "${this::class.java.simpleName} ${this::class.java.hashCode()} setFragmentManager: "
        )
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
                .replace(R.id.container, ChooserFragment.newInstance(), ProductsFragment.TAG)
                .commitNow()
        }
    }

    override fun openProductsScan() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack(ChooserFragment.TAG)
                .replace(R.id.container, ProductsFragment.newInstance(), ProductsFragment.TAG)
                .commit()
        }
    }

    override fun openStagesScan() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack(ChooserFragment.TAG)
                .replace(R.id.container, SendStatusFragment.newInstance(), SendStatusFragment.TAG)
                .commit()
        }
    }

    override fun openScanner() {
        commitOnActiveFm {
            it.beginTransaction()
                .addToBackStack("openScanner")
                .replace(R.id.container, ScannerFragment.newInstance(), ScannerFragment.TAG)
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