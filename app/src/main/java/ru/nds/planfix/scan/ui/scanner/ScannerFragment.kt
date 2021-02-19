package ru.nds.planfix.scan.ui.scanner

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import ru.nds.planfix.scan.MainActivity
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.ScanerFragmentBinding

class ScannerFragment : Fragment() {

    companion object {
        const val TAG = "ScannerFragment"
        fun newInstance() = ScannerFragment()
    }

    private val scanHandler = object : ZXingScannerView.ResultHandler {
        override fun handleResult(rawResult: Result?) {
            Log.d(
                "APP_TAG",
                "${this::class.java.simpleName} ${this::class.java.hashCode()} rawResult: $rawResult"
            );
            Toast.makeText(requireContext(), rawResult.toString(), Toast.LENGTH_SHORT).show();
            (activity as? MainActivity)?.codeScannedListener?.onCodeScanned(rawResult?.text ?: "")
            requireActivity().supportFragmentManager.popBackStack()
        }
    }

    private var binding: ScanerFragmentBinding? = null

    private lateinit var viewModel: ScannerViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.scaner_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(ScannerViewModel::class.java)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = ScanerFragmentBinding.bind(view)

    }

    override fun onResume() {
        super.onResume()
        binding?.zxing?.also {
            it.setResultHandler(scanHandler)
            it.startCamera()
        }
    }

    override fun onPause() {
        binding?.zxing?.stopCamera()
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }

}