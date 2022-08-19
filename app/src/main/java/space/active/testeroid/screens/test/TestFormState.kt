package space.active.testeroid.screens.test

data class TestFormState (
    var id: String = "",
    var title: String = "",
    val correctList: ArrayList<AnswerColor> = List (4) { AnswerColor.Neutral } as ArrayList<AnswerColor>,
    var variant1: String = "",
    var variant2: String = "",
    var variant3: String = "",
    var variant4: String = "",
    var count: String = "",
    var size: String = "",
    var score: String = "",
)

enum class AnswerColor {
    Ok, NotOk, Neutral
}