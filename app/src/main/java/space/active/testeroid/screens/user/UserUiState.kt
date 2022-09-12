package space.active.testeroid.screens.user

import space.active.testeroid.db.modelsdb.Users

sealed class UserUiState {
    data class SelectedUser(val user: Users): UserUiState()
    data class ShowError(val msg: Int): UserUiState()
    object ShowInputPasswordDialog: UserUiState()
    object OpenUserEdit: UserUiState()
}