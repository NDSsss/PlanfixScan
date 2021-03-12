package ru.nds.planfix.base

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import ru.nds.planfix.base.databinding.IncludeBaseFullScreenMessageBinding

abstract class BaseFragment<Vm : BaseViewModel>(
    @LayoutRes layoutId: Int
) : Fragment(layoutId) {
    abstract val viewModel: Vm
    private val parentContainer: ConstraintLayout
        get() = (requireView() as? ConstraintLayout)
            ?: throw IllegalStateException("Root view of ${this::class.java.simpleName} must be ConstraintLayout")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.screenState.observe(viewLifecycleOwner, ::handleScreenState)
    }

    protected open fun handleScreenState(state: ScreenState) {
        if (state is BaseScreenState) {
            when (state) {
                BaseScreenState.Loading -> {
                    showLoading()
                }
                is BaseScreenState.FullScreenMessage -> {
                    showFullScreenMessage(state.messageData)
                }
                BaseScreenState.ShowContent -> {
                    hideLoading()
                    hideFullScreenMessage()
                }
            }
        } else {
            handleCustomScreenState(state)
        }
    }

    protected open fun handleCustomScreenState(state: ScreenState) {

    }

    protected fun showFullScreenMessage(fullScreenMessageData: FullScreenMessageData) {
        hideLoading()
        val title: String? = fullScreenMessageData.titleRes?.let { resources.getString(it) }
            ?: fullScreenMessageData.title
        val message: String? = fullScreenMessageData.messageRes?.let { resources.getString(it) }
            ?: fullScreenMessageData.message
        val actionName: String? =
            fullScreenMessageData.actionNameRes?.let { resources.getString(it) }
                ?: fullScreenMessageData.actionName
        val messageBinding = IncludeBaseFullScreenMessageBinding.inflate(
            layoutInflater,
            parentContainer,
            false
        )
        if (title != null) {
            messageBinding.baseFullScreenMessageTitle.isVisible = true
            messageBinding.baseFullScreenMessageTitle.text = title
        } else {
            messageBinding.baseFullScreenMessageTitle.isVisible = false
        }
        if (message != null) {
            messageBinding.baseFullScreenMessageMessage.isVisible = true
            messageBinding.baseFullScreenMessageMessage.text = message
        } else {
            messageBinding.baseFullScreenMessageMessage.isVisible = false
        }
        if (actionName != null) {
            messageBinding.baseFullScreenMessageAction.isVisible = true
            messageBinding.baseFullScreenMessageAction.text = actionName
            messageBinding.baseFullScreenMessageAction.setOnClickListener {
                fullScreenMessageData.action?.invoke()
            }
        } else {
            messageBinding.baseFullScreenMessageAction.isVisible = false
        }
        parentContainer.addView(messageBinding.root)
    }

    protected fun showLoading() {
        hideFullScreenMessage()
        val loadingView = layoutInflater.inflate(
            R.layout.include_base_loading_view,
            parentContainer,
            false
        )
        //Mb some customise
        parentContainer.addView(loadingView)
    }

    protected fun hideLoading() {
        val loadingView = parentContainer.findViewById<ConstraintLayout?>(R.id.baseLoadingContainer)
        parentContainer.removeView(loadingView)
    }

    protected fun hideFullScreenMessage() {
        val messageView =
            parentContainer.findViewById<ConstraintLayout?>(R.id.baseFullScreenMessageContainer)
        parentContainer.removeView(messageView)
    }
}

open class ScreenState

sealed class BaseScreenState : ScreenState() {
    object Loading : BaseScreenState()
    data class FullScreenMessage(val messageData: FullScreenMessageData) : BaseScreenState() {
        companion object {
            fun createTestMessage() = FullScreenMessage(
                messageData = FullScreenMessageData(
                    title = "Test title",
                    message = "Test Message",
                    actionName = "Text action"
                )
            )
        }
    }

    object ShowContent : BaseScreenState()
}

data class FullScreenMessageData(
    @StringRes val titleRes: Int? = null,
    val title: String? = null,
    @StringRes val messageRes: Int? = null,
    val message: String? = null,
    @StringRes val actionNameRes: Int? = null,
    val actionName: String? = null,
    val action: (() -> Unit)? = null
)