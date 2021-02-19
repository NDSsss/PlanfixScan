package ru.nds.planfix.scan.ui.settings

import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.subjects.BehaviorSubject
import ru.nds.planfix.scan.data.IPrefsStorage

class SettingsViewModel : ViewModel() {

    val authSubject = BehaviorSubject.create<String>()
    val sidSubject = BehaviorSubject.create<String>()

    var prefs: IPrefsStorage? = null
    set(value) {
        field = value
        authSubject.onNext(field?.authHeader)
        sidSubject.onNext(field?.sid)
    }

    fun generateAuth(apiKey: String, token: String){
        prefs?.generateAuth(apiKey, token)
        authSubject.onNext(prefs?.authHeader?:"null")
    }
}