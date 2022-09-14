package space.active.testeroid.screens.useredit

data class UserEditFormState(
    var id: String = "",
    var title: String = "",
    var username: Username = Username(),
    var password: Password = Password(),
    var okEnabled: Boolean = false,
    var administrator: Administrator = Administrator(),
    var deleteEnabled: Boolean = false,
    var selectedEnabled: Boolean = true,
)

data class Username (
    var text: String = "",
    var enabled: Boolean = false
)
data class Password (
    var text: String = "",
    var enabled: Boolean = false
)
data class Administrator (
    var checked: Boolean = false,
    var enabled: Boolean = false
)
