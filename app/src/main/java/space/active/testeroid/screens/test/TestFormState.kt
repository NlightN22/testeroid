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
    var restartVisibility: Boolean = false
)

data class VariantState (
    var text: String = "",
    var enabled: Boolean = true,
    var correct: AnswerColor = AnswerColor.Neutral
        )

enum class AnswerColor {
    Ok, NotOk, Neutral
}
