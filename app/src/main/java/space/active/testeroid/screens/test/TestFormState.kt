package space.active.testeroid.screens.test

import android.opengl.Visibility
import space.active.testeroid.helpers.UiText

data class TestFormState (
    var id: String = "",
    var title: UiText = UiText.DynamicString(""),
    var variants: ArrayList<VariantState> = List(4) {VariantState()} as ArrayList<VariantState>,
    var count: String = "",
    var size: String = "",
    var score: String = "",
    var restartVisibility: Boolean = false,
    var submitEnabled: Boolean = true
)

data class VariantState (
    var text: String = "",
    var enabled: Boolean = true,
    var color: AnswerColor = AnswerColor.Neutral,
    var selected: Boolean = false,
        )

enum class AnswerColor {
    Ok, NotOk, Neutral, Selected
}
