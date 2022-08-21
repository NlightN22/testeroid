package space.active.testeroid.screens.score


data class ScoreFormState (
    var title: Boolean = false,
    var username: String = "",
    var score: String = "",
    var paramsVisibility: Boolean = false,
    var correctScore: String = "",
    var notCorrectScore: String = "",
)

