package space.active.testeroid.screens.score

import space.active.testeroid.screens.test.TestUiState

sealed class ScoreFormEvents {
    data class SubmitParams(val correct: String, val notCorrect: String): ScoreFormEvents()
}