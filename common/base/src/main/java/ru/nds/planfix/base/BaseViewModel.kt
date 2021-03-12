package ru.nds.planfix.base

import androidx.lifecycle.LiveData

interface BaseViewModel {
    val screenState: LiveData<ScreenState>
}