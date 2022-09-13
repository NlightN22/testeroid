package space.active.testeroid.screens.edittestlist

sealed class EditTestListUiState {
    object SelectedItem: EditTestListUiState()
    object NotSelectedItem: EditTestListUiState()
}
