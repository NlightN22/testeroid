package space.active.testeroid.screens.user

import space.active.testeroid.db.modelsdb.Users

sealed class UserUiState {
    data class SelectedUser(val userId: Long): UserUiState()
    data class ShowError(val msg: String): UserUiState()
    object ShowInputPasswordDialog: UserUiState()
    object OpenUserEdit: UserUiState()
}