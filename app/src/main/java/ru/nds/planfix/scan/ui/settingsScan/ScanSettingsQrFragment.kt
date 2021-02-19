package ru.nds.planfix.scan.ui.settingsScan

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import ru.nds.planfix.scan.MainActivity
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.databinding.FragmentScanSettingsQrBinding

class ScanSettingsQrFragment : Fragment(R.layout.fragment_scan_settings_qr) {

    private var binding: FragmentScanSettingsQrBinding? = null


    private val scanHandler = object : ZXingScannerView.ResultHandler {
        override fun handleResult(rawResult: Result?) {
            Log.d(
                "APP_TAG",
                "${this::class.java.simpleName} ${this::class.java.hashCode()} settings scan result: $rawResult"
            );
            (activity as? MainActivity)?.settingsQrScannedListener?.onSettingsQrScanned(
                rawResult?.text ?: ""
            )
            requireActivity().supportFragmentManager.popBackStack()
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentScanSettingsQrBinding.bind(view)
    }

    override fun onResume() {
        super.onResume()
        binding?.scannerView?.apply {
            setResultHandler(scanHandler)
            startCamera()
        }
    }

    override fun onPause() {
        binding?.scannerView?.apply {
            stopCamera()
        }
        super.onPause()
    }

    override fun onDestroyView() {
        binding = null
        super.onDestroyView()
    }


    companion object {
        const val TAG = "ScanSettingsQrFragment"
        fun newInstance() = ScanSettingsQrFragment()
    }
}