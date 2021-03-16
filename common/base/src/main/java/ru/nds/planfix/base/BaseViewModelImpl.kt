package ru.nds.planfix.base

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.disposables.CompositeDisposable
import ru.nds.planfix.network.isInternetError
import ru.nds.planfix.notifications.NotificationsManager
import ru.nds.planfix.scan.appResources.AppResources

open class BaseViewModelImpl(
    protected val appResources: AppResources,
    protected val notificationsManager: NotificationsManager,
) : ViewModel(), BaseViewModel {

    protected val disposables = CompositeDisposable()

    override val screenState: MutableLiveData<ScreenState> = MutableLiveData()
//        MutableLiveData(BaseScreenState.ShowContent)
//        MutableLiveData(BaseScreenState.Loading)
//        MutableLiveData(BaseScreenState.FullScreenMessage.createTestMessage())

    override fun onCleared() {
        disposables.dispose()
        super.onCleared()
    }

    protected fun <T : Any> Single<T>.withProcessing(
        isInitialRequest: Boolean = false,
        reInitAction: (() -> Unit)? = null
    ): Single<T> =
        this
            .doOnSubscribe {
                screenState.postValue(BaseScreenState.Loading)
            }
            .doOnError {
                when {
                    it.isInternetError() && isInitialRequest -> {
                        screenState.postValue(
                            BaseScreenState.FullScreenMessage(
                                FullScreenMessageData(
                                    title = appResources.getString(R.string.internet_error_title),
                                    message = appResources.getString(R.string.internet_error_message),
                                    actionName = appResources.getString(R.string.internet_error_action),
                                    action = reInitAction
                                )
                            )
                        )
                    }
                    isInitialRequest -> {
                        screenState.postValue(
                            BaseScreenState.FullScreenMessage(
                                FullScreenMessageData(
                                    title = appResources.getString(R.string.unknown_error_title),
                                    message = appResources.getString(R.string.unknown_error_message),
                                    actionName = appResources.getString(R.string.unknown_error_action),
                                    action = reInitAction
                                )
                            )
                        )
                    }
                    else -> {
                        notificationsManager.showNotification(it.message)
                    }
                }
            }
            .doOnSuccess {
                screenState.postValue(BaseScreenState.ShowContent)
            }

    fun Completable.withProcessing(
        isInitialRequest: Boolean = false,
        reInitAction: (() -> Unit)? = null
    ): Completable = this
        .doOnSubscribe {
            screenState.postValue(BaseScreenState.Loading)
        }
        .doOnError {
            when {
                it.isInternetError() && isInitialRequest -> {
                    screenState.postValue(
                        BaseScreenState.FullScreenMessage(
                            FullScreenMessageData(
                                title = appResources.getString(R.string.internet_error_title),
                                message = appResources.getString(R.string.internet_error_message),
                                actionName = appResources.getString(R.string.internet_error_action),
                                action = reInitAction
                            )
                        )
                    )
                }
                isInitialRequest -> {
                    screenState.postValue(
                        BaseScreenState.FullScreenMessage(
                            FullScreenMessageData(
                                title = appResources.getString(R.string.unknown_error_title),
                                message = appResources.getString(R.string.unknown_error_message),
                                actionName = appResources.getString(R.string.unknown_error_action),
                                action = reInitAction
                            )
                        )
                    )
                }
                else -> {
                    notificationsManager.showNotification(it.message)
                }
            }
        }
        .doOnComplete {
            screenState.postValue(BaseScreenState.ShowContent)
        }

}