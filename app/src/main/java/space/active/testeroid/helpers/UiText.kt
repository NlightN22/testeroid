package space.active.testeroid.helpers

import android.content.Context

sealed class UiText {
    data class DynamicString(val value: String): UiText()
    class StringResource(
        val resId: Int,
        vararg val args: Any
    ): UiText()

    fun asString(context: Context): String {
        return when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }
    }
}
