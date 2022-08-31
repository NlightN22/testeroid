package space.active.testeroid.screens.useredit

import space.active.testeroid.db.modelsdb.Users

sealed class UserEditUiState {
    object NewUser: UserEditUiState()
    object EditUser: UserEditUiState()
    object RestoreForm: UserEditUiState()
    data class ErrorMessage (var msg: String): UserEditUiState()
}
