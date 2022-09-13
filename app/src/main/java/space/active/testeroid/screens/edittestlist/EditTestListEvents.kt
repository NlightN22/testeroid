package space.active.testeroid.screens.edittestlist

sealed class EditTestListEvents{
    data class OnItemClick(val itemId: Long): EditTestListEvents()
    data class OnItemLongClick(val itemId: Long): EditTestListEvents()
    object OnAddClick: EditTestListEvents()
    object OnDeleteClick: EditTestListEvents()
}
