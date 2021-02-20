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
import androidx.lifecycle.ViewModelProvider
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.nds.planfix.scan.data.ProductsPrefs
import ru.nds.planfix.scan.data.StagesPrefs
import ru.nds.planfix.scan.models.CodeModel
import ru.nds.planfix.scan.ui.chooser.ChooserFragment
import ru.nds.planfix.scan.ui.codes.CodesFragment
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment.Companion.SETTINGS_PRODUCTS
import ru.nds.planfix.scan.ui.settingsScan.ScanSettingsQrFragment.Companion.SETTINGS_STAGES
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val vmBinds = CompositeDisposable()
    val codesList: MutableList<CodeModel> = mutableListOf()

    private lateinit var viewModel: MainActivityViewModel
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
        initVm()
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            startMainFlow()
        }
        vmBinds.addAll(
            viewModel.actionSuccessSubject.subscribe {
                Toast.makeText(this, "Успешно", Toast.LENGTH_SHORT).show()
            },
            viewModel.actionFailSubject.subscribe { error ->
                Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
            }
        )
    }

    private fun initVm() {
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.productPrefs = ProductsPrefs(this)
        viewModel.stagesPrefs = StagesPrefs(this)
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
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, ChooserFragment.newInstance(), CodesFragment.TAG)
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