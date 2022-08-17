package space.active.testeroid.screens.edittest

data class EditTestFormState (
    var id: String = "",
    var title: String = "",
    var listVariants: ArrayList<String> = List(4) {""} as ArrayList<String>,
    var listSelected: ArrayList<Boolean> = List(4) {false} as ArrayList<Boolean>,
    )