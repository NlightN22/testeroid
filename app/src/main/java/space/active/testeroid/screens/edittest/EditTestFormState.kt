package space.active.testeroid.screens.edittest

data class EditTestFormState (
    var id: String = "",
    var title: String = "",
    var variant1: String = "",
    var variant2: String = "",
    var variant3: String = "",
    var variant4: String = "",
    var listSelected: ArrayList<Boolean> = List(4) {false} as ArrayList<Boolean>,
    )