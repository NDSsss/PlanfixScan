package ru.nds.planfix.coordinator

import androidx.fragment.app.FragmentManager

interface SetUpCoordinator {
    fun setFragmentManager(fm: FragmentManager)
    fun removeFragmentManager()
}