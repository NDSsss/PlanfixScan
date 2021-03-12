package ru.nds.planfix.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

open class BaseViewModelImpl : ViewModel(), BaseViewModel {
    override val screenState: MutableLiveData<ScreenState> = MutableLiveData()
//        MutableLiveData(BaseScreenState.ShowContent)
//        MutableLiveData(BaseScreenState.Loading)
//        MutableLiveData(BaseScreenState.FullScreenMessage.createTestMessage())
}