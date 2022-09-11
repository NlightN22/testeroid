package space.active.testeroid.screens.useredit

data class UserEditFormState(
    var id: String = "",
    var title: String = "",
    var username: String = "",
    var password: String = "",
    var administrator: Boolean = false,
    var adminEnabled: Boolean = true,
    var deleteEnabled: Boolean = true,
    var selectedEnabled: Boolean = true,
)
