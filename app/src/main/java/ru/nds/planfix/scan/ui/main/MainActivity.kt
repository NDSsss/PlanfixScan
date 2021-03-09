package ru.nds.planfix.scan.ui.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.koin.android.viewmodel.ext.android.viewModel
import ru.nds.planfix.scan.R

class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel<MainActivityViewModelImpl>()

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
                viewModel.openProductSettingsScanner()
                true
            }
            R.id.menu_settings_stages -> {
                viewModel.openStagesSettingsScanner()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    private fun startMainFlow() {
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
            viewModel.onCameraPermissionsGranted()
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

    override fun onBackPressed() {
        if (supportFragmentManager.popBackStackImmediate().not()) {
            super.onBackPressed()
        }
    }

    private companion object {
        private const val REQUEST_CAMERA_PERMISSION = 12345
    }
}