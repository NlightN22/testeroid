package space.active.testeroid.screens.main

data class MainActivityFormState
    (
    var navigation: Navigation = Navigation(),
    var tabs: Tabs = Tabs()
)
{
    data class Navigation
        (
        var visibility: Boolean = false,
        var add: Boolean = true,
        var edit: Boolean = true,
        var delete: Boolean = true)

    data class Tabs (
        var visibility: Boolean = true
            )
}
