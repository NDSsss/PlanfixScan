package ru.nds.planfix.scan.appResources

import androidx.annotation.*

interface AppResources {

    fun getString(@StringRes id: Int): String

    fun getString(@StringRes id: Int, args: Any): String

    fun getString(@StringRes id: Int, vararg args: Any?): String

    fun getQuantityString(@PluralsRes id: Int, quantity: Int): String

    fun getQuantityString(@PluralsRes id: Int, quantity: Int, vararg args: Any?): String

    @ColorInt
    fun getColor(@ColorRes id: Int): Int

    @Px
    fun getDimension(@DimenRes id: Int): Float

    companion object {
        const val ID_NULL = 0
    }
}