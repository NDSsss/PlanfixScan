package ru.nds.planfix.scan.ui.scanner

import androidx.fragment.app.Fragment
import me.dm7.barcodescanner.zxing.ZXingScannerView
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.ScanerFragmentBinding
import ru.nds.planfix.scan.ui.utils.viewBinding

class ScannerFragment : Fragment(R.layout.scaner_fragment) {

    companion object {
        const val TAG = "ScannerFragment"
        fun newInstance() = ScannerFragment()
    }

    private val scanHandler = ZXingScannerView.ResultHandler { rawResult ->
        viewModel.handleResult(rawResult)
    }

    private val binding: ScanerFragmentBinding by viewBinding()

    private val viewModel: ScannerViewModel by viewModel<ScannerViewModelImpl>()

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