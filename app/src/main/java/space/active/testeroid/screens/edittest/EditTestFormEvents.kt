package space.active.testeroid.screens.edittest

sealed class EditTestFormEvents {
    data class TitleChanged(val title: String): EditTestFormEvents()
    data class Variant1(val text: String): EditTestFormEvents()
    data class Variant2(val text: String): EditTestFormEvents()
    data class Variant3(val text: String): EditTestFormEvents()
    data class Variant4(val text: String): EditTestFormEvents()
    data class CheckChanged(val index: Int): EditTestFormEvents()
    object Submit: EditTestFormEvents()
    object Cancel: EditTestFormEvents()
}