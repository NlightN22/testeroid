package space.active.testeroid.screens.score

sealed class ScoreUiState {
    data class UserScore(val userId: Long?): ScoreUiState()
    object UpdateParams: ScoreUiState()
    object Empty:ScoreUiState()
}