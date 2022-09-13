package space.active.testeroid.screens.useredit

import space.active.testeroid.db.modelsdb.Users

sealed class UserEditEvents {
    data class OpenFragment(val userForEdit: Users?): UserEditEvents()
    object OnOkClick: UserEditEvents()
    object OnCancelClick: UserEditEvents()
    object OnDeleteClick: UserEditEvents()
    object OnSelectClick: UserEditEvents()
    data class OnEditUsername(val string: String): UserEditEvents()
    data class  OnEditPassword(val string: String): UserEditEvents()
    object OnAdminCheckboxClick: UserEditEvents()
}