package de.halfbit.co2monitor.commons

import android.content.res.Resources
import androidx.annotation.StringRes
import java.io.Serializable

interface StringRef : Serializable {
    fun resolve(resources: Resources): String
}

fun stringRef(@StringRes value: Int): StringRef = ResolvableStringRef(value)
fun stringRef(value: String): StringRef = StringStringRef(value)

private data class ResolvableStringRef(@StringRes val stringId: Int) : StringRef {
    override fun resolve(resources: Resources): String = resources.getString(stringId)
}

private data class StringStringRef(val value: String) : StringRef {
    override fun resolve(resources: Resources): String = value
}
