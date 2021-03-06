package ru.nds.planfix.scan.ui.settings

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject

class SettingsViewModel : ViewModel() {

    val authSubject = BehaviorSubject.create<String>()
    val sidSubject = BehaviorSubject.create<String>()
}