package space.active.testeroid.screens.useredit

import space.active.testeroid.screens.user.UserViewModel

data class UserEditFormState(
    var id: String = "",
    var title: String = "",
    var username: String = "",
    var password: String = "",
    var administrator: Boolean = false,
    var deleteVisible: Boolean = true,
)
