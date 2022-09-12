package space.active.testeroid.screens.useredit

import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.UiText

sealed class UserEditUiState {
    object NewUser: UserEditUiState()
    object ViewUser: UserEditUiState()
    object EditUser: UserEditUiState()
    object RestoreForm: UserEditUiState()
    object LastAdmin: UserEditUiState()
    class ShowError (val uiText: UiText): UserEditUiState()
}
