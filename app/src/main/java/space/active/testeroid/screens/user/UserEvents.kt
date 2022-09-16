package space.active.testeroid.screens.user

sealed class UserEvents {
    data class OnClickItem(val userId: Long) : UserEvents()
    data class OnLongClickItem(val userId: Long) : UserEvents()
    data class OnCheckBoxClick(val userId: Long) : UserEvents()
    data class OkDialogPassword(val password: String) : UserEvents()
    object OnAddClick : UserEvents()
    object CancelDialogPassword : UserEvents()
}