package ru.nds.planfix.scan

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import ru.nds.planfix.scan.data.PrefsStorage
import ru.nds.planfix.scan.models.CodeModel
import ru.nds.planfix.scan.ui.codes.CodesFragment
import ru.nds.planfix.scan.ui.scanner.ScannerFragment
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    var codeScannedListener: CodeScannedListener? = null
    var settingsQrScannedListener: SettingsQrScannedListener? = null

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
            R.id.menu_settings -> {
                openSettings()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun startMainFlow() {
        if (checkExpired()) {
            YandexMetricaActions.onCompanyExpired(PrefsStorage(this).account)
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, CodesFragment.newInstance(), CodesFragment.TAG)
                .commitNow()
        }
    }

    private fun checkExpired(): Boolean {
        val expiredDate = "09/03/2021"
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

    //    val codesList = generateFakeCodes().toMutableList()
    val codesList: MutableList<CodeModel> = mutableListOf()

    fun openScanner() {
        supportFragmentManager.beginTransaction()
            .hide(supportFragmentManager.findFragmentByTag(CodesFragment.TAG)!!)
            .add(R.id.container, ScannerFragment.newInstance(), ScannerFragment.TAG)
//            .replace(R.id.container, ScannerFragment.newInstance(), ScannerFragment.TAG)
            .addToBackStack(OPEN_SCANNER_TAG)
            .commit()
    }

    fun openSettings() {
        supportFragmentManager.beginTransaction()
            .hide(supportFragmentManager.findFragmentByTag(CodesFragment.TAG)!!)
            .add(R.id.container, ScanSettingsQrFragment.newInstance(), ScanSettingsQrFragment.TAG)
//            .replace(R.id.container, ScannerFragment.newInstance(), ScannerFragment.TAG)
            .addToBackStack(OPEN_SETTINGS_TAG)
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
    fun onSettingsQrScanned(configJson: String)
}