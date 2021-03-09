package ru.nds.planfix.scan.appResources

import android.content.res.Resources
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.content.res.ResourcesCompat

class AndroidAppResources(private val resources: Resources) : AppResources {

    override fun getString(@StringRes id: Int): String {
        require(id != AppResources.ID_NULL) { "String resource reference must not be null" }
        return resources.getString(id)
    }

    override fun getString(@StringRes id: Int, args: Any): String {
        require(id != AppResources.ID_NULL) { "String resource reference must not be null" }
        return resources.getString(id, args)
    }

    @Suppress("SpreadOperator")
    override fun getString(@StringRes id: Int, vararg args: Any?): String {
        require(id != AppResources.ID_NULL) { "String resource reference must not be null" }
        return resources.getString(id, *args)
    }

    override fun getQuantityString(@PluralsRes id: Int, quantity: Int): String {
        require(id != AppResources.ID_NULL) { "Plurals string resource reference must not be null" }
        return resources.getQuantityString(id, quantity)
    }

    @Suppress("SpreadOperator")
    override fun getQuantityString(id: Int, quantity: Int, vararg args: Any?): String {
        require(id != AppResources.ID_NULL) { "Plurals string resource reference must not be null" }
        return resources.getQuantityString(id, quantity, *args)
    }

    override fun getColor(id: Int): Int {
        require(id != AppResources.ID_NULL) { "Color resource reference must not be null" }
        return ResourcesCompat.getColor(resources, id, null)
    }

    override fun getDimension(id: Int): Float {
        require(id != AppResources.ID_NULL) { "Dimension resource reference must not be null" }
        return resources.getDimension(id)
    }
}