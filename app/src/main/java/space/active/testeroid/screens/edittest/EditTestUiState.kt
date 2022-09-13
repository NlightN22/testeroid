package space.active.testeroid.screens.edittest

sealed class EditTestUiState {
    object ShowNew : EditTestUiState()
    data class ShowIncome (val testId: Long): EditTestUiState()
    object ShowEdited: EditTestUiState()
    data class Error (val message: Int): EditTestUiState()
}