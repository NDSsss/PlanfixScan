package ru.nds.planfix.scan.ui.navigation

import androidx.fragment.app.FragmentManager

interface SetUpCoordinator {
    fun setFragmentManager(fm: FragmentManager)
    fun removeFragmentManager()
}