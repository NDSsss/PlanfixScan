package ru.nds.planfix.scan.ui.main

import android.app.Activity
import androidx.fragment.app.FragmentManager

interface MainActivityViewModel {

    fun setFragmentManager(fm: FragmentManager)
    fun removeFragmentManager()
    fun setActivity(activity: Activity)
    fun removeActivity()

    fun onCameraPermissionsGranted()
    fun openProductSettingsScanner()
    fun openStagesSettingsScanner()
    fun openTasksSettingsScanner()
}