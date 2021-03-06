package ru.nds.planfix.scan.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.scan.R
import ru.nds.planfix.scan.YandexMetricaActions
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment.Companion.SETTINGS_PRODUCTS
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment.Companion.SETTINGS_STAGES
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel<MainActivityViewModelImpl>()
    var codeScannedListener: CodeScannedListener? = null
    var settingsQrScannedListener = object : SettingsQrScannedListener {
        override fun onProductSettingsQrScanned(configJson: String) {
            viewModel.onProductSettingsQrScanned(configJson)
        }

        override fun onStagesSettingQrScanned(configJson: String) {
            viewModel.onStagesSettingQrScanned(configJson)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            startMainFlow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.menu_settings_product -> {
                openSettings(SETTINGS_PRODUCTS)
                true
            }
            R.id.menu_settings_stages -> {
                openSettings(SETTINGS_STAGES)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun startMainFlow() {
        if (checkExpired()) {
            YandexMetricaActions.onCompanyExpired(ProductsPrefs(this).account)
            Toast.makeText(
                this,
                "Истек срок пробной версии, обратитесь к администратору",
                Toast.LENGTH_LONG
            ).show()
            return
        }
        if (ContextCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_CAMERA_PERMISSION
            )
        } else {
            viewModel.openChooser()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.setFragmentManager(supportFragmentManager)
        viewModel.setActivity(this)
    }

    override fun onPause() {
        viewModel.removeFragmentManager()
        viewModel.removeActivity()
        super.onPause()
    }

    private fun checkExpired(): Boolean {
        val expiredDate = "21/03/2021"
        val format = SimpleDateFormat("dd/MM/yyyy")
        return Date().after(format.parse(expiredDate))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            REQUEST_CAMERA_PERMISSION -> if (grantResults.getOrNull(0) == PackageManager.PERMISSION_GRANTED) {
                startMainFlow()
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA_PERMISSION
                )
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    private fun openSettings(settingsType: Int) {
        supportFragmentManager.beginTransaction()
            .addToBackStack("openSettings")
            .replace(
                R.id.container,
                ScanSettingsQrFragment.newInstance(settingsType),
                ScanSettingsQrFragment.TAG
            )
            .commit()
    }

    override fun onBackPressed() {
        if (supportFragmentManager.popBackStackImmediate().not()) {
            super.onBackPressed()
        }
    }

    private companion object {
        private const val OPEN_SCANNER_TAG = "OPEN_SCANNER_TAG"
        private const val OPEN_SETTINGS_TAG = "OPEN_SETTINGS_TAG"
        private const val REQUEST_CAMERA_PERMISSION = 12345
    }
}

interface CodeScannedListener {
    fun onCodeScanned(code: String)
}

interface SettingsQrScannedListener {
    fun onProductSettingsQrScanned(configJson: String)
    fun onStagesSettingQrScanned(configJson: String)
}