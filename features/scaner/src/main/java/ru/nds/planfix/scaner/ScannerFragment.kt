package ru.nds.planfix.scaner

import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.base.BaseFragment
import ru.nds.planfix.binding.viewBinding
import ru.nds.planfix.scaner.databinding.ScanerFragmentBinding

class ScannerFragment : BaseFragment<ScannerViewModel>(R.layout.scaner_fragment) {

    companion object {
        const val TAG = "ScannerFragment"
        fun newInstance() = ScannerFragment()
    }

    private val scanHandler = ZXingScannerView.ResultHandler { rawResult ->
        viewModel.handleResult(rawResult)
    }

    private val binding: ScanerFragmentBinding by viewBinding()

    override val viewModel: ScannerViewModel by viewModel<ScannerViewModelImpl>()

    override fun onResume() {
        super.onResume()
        binding.zxing.also {
            it.setResultHandler(scanHandler)
            it.startCamera()
        }
    }

    override fun onPause() {
        binding.zxing.stopCamera()
        super.onPause()
    }

}