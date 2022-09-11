package space.active.testeroid.screens.useredit

sealed class UserEditEvents {
    data class OpenFragment(val userId: Long?): UserEditEvents()
    object OnOkClick: UserEditEvents()
    object OnCancelClick: UserEditEvents()
    object OnDeleteClick: UserEditEvents()
    object OnSelectClick: UserEditEvents()
    data class OnEditUsername(val string: String): UserEditEvents()
    data class  OnEditPassword(val string: String): UserEditEvents()
    object OnAdminCheckboxClick: UserEditEvents()
}
