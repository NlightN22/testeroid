package space.active.testeroid.screens.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.active.testeroid.DATA_BASE_NAME
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.helpers.SingleLiveEvent

class MainActivityViewModel(private val repository: RepositoryRealization): ViewModel() {

    var bottomToolBarVisibility = MutableLiveData<Boolean>(false)
    var bottomItemsVisibility = MutableLiveData(
        ViewStateMain.BottomToolBarButtons(
            add = false,
            edit = true,
            delete = true
        )
    )
    var bottomTabsVisibility = MutableLiveData<Boolean>(true)

    private val _addClick = SingleLiveEvent<Any>()
    val addClick: LiveData<Any> get() = _addClick
    private val _editClick = SingleLiveEvent<Any>()
    val editClick: LiveData<Any> get() = _editClick
    private val _deleteClick = SingleLiveEvent<Any>()
    val deleteClick: LiveData<Any> get() = _deleteClick

    fun deleteDataBase(context: Context) {
        context.deleteDatabase("$DATA_BASE_NAME") // This can delete Database if necessary
    }

    fun setViewState(viewState: ViewStateMain) {
        when (viewState) {
            is ViewStateMain.BottomToolBar -> {
                bottomToolBarVisibility.value = viewState.visible
                bottomTabsVisibility.value = !bottomToolBarVisibility.value!!
            }
            is ViewStateMain.BottomToolBarButtons -> {
                bottomItemsVisibility.value!!.apply {
                    add = viewState.add
                    edit = viewState.edit
                    delete = viewState.delete
                }
                bottomItemsVisibility.notifyObserver()
            }
        }
    }

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun addOnClick (){
        _addClick.call()
    }

    fun editOnClick (){
        _editClick.call()
    }

    fun deleteOnClick (){
        _deleteClick.call()
    }

    sealed class ViewStateMain{
        data class BottomToolBar(var visible: Boolean): ViewStateMain()
        data class BottomToolBarButtons(var add: Boolean, var edit: Boolean, var delete: Boolean): ViewStateMain()
    }

}