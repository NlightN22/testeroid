package space.active.testeroid.screens.edittest

sealed class EditTestFormEvents {
    data class TitleChanged(val title: String): EditTestFormEvents()
    data class VariantChanged(val variant: String, val index: Int): EditTestFormEvents()
    data class CheckChanged(val index: Int): EditTestFormEvents()
    object Submit: EditTestFormEvents()
    object Cancel: EditTestFormEvents()
}