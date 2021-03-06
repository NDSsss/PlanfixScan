package ru.nds.planfix.scan.ui.main

import android.app.Activity
import androidx.fragment.app.FragmentManager

interface MainActivityViewModel {

    fun setFragmentManager(fm: FragmentManager)
    fun removeFragmentManager()
    fun setActivity(activity: Activity)
    fun removeActivity()

    fun onProductSettingsQrScanned(configJson: String)
    fun onStagesSettingQrScanned(configJson: String)

    fun openChooser()
}