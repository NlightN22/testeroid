package space.active.testeroid.screens.test

data class TestFormState (
    var id: String = "",
    var title: String = "",
    var variants: ArrayList<VariantState> = List(4) {VariantState()} as ArrayList<VariantState>,
    var count: String = "",
    var size: String = "",
    var score: String = "",
)

data class VariantState (
    var text: String = "",
    var enabled: Boolean = true,
    var correct: AnswerColor = AnswerColor.Neutral
        )

enum class AnswerColor {
    Ok, NotOk, Neutral
}
