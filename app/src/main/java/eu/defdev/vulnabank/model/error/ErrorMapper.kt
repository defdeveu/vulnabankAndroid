package eu.defdev.vulnabank.model.error

import androidx.annotation.StringRes
import eu.defdev.vulnabank.R
import java.net.UnknownHostException

object ErrorMapper {

    @StringRes
    fun parseError(throwable: Throwable?): Int{
        return when (throwable) {
            is UnknownHostException -> R.string.no_internet
            else -> R.string.general
        }
    }
}