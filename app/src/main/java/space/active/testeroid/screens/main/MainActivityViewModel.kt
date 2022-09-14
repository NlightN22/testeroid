package space.active.testeroid.screens.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.Flow
import space.active.testeroid.DATA_BASE_NAME
import space.active.testeroid.helpers.SingleLiveEvent
import space.active.testeroid.helpers.notifyObserver
import space.active.testeroid.repository.DataStoreRepository

class MainActivityViewModel(
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    private val _form = MutableLiveData<MainActivityFormState>(MainActivityFormState())
    val form: LiveData<MainActivityFormState> = _form

    val userIdDataStore: Flow<Long?> = dataStore.userId
    val userAdminDataStore: Flow<Boolean> = dataStore.admin

    fun uiState(state: MainActivityUiState) {
        when (state) {
            is MainActivityUiState.ShowModalFragment -> {
                _form.value?.let { form->
                    form.pager.visibility = false
                    uiState(MainActivityUiState.HideBottom)
                    _form.notifyObserver()
                }
            }
            is MainActivityUiState.CloseModalFragment -> {
                _form.value?.let { form->
                    form.pager.visibility = true
                    uiState(MainActivityUiState.ShowTabs)
                    _form.notifyObserver()
                }
            }
            is MainActivityUiState.ShowNavigation -> {
                _form.value?.let { form ->
                    form.tabs.visibility = false
                    form.navigation.visibility = true
                    form.navigation.add = state.navigation.add
                    form.navigation.edit = state.navigation.edit
                    form.navigation.delete = state.navigation.delete
                    _form.notifyObserver()
                }
            }
            is MainActivityUiState.ShowTabs -> {
                _form.value?.let { form->
                    form.tabs.visibility = true
                    form.navigation.visibility = false
                    _form.notifyObserver()
                }
            }
            is MainActivityUiState.HideBottom -> {
                _form.value?.let {
                    it.tabs.visibility = false
                    it.navigation.visibility = false
                    _form.notifyObserver()
                }
            }
        }
    }

    private val _addClick = SingleLiveEvent<Any>()
    val addClick: LiveData<Any> get() = _addClick
    private val _editClick = SingleLiveEvent<Any>()
    val editClick: LiveData<Any> get() = _editClick
    private val _deleteClick = SingleLiveEvent<Any>()
    val deleteClick: LiveData<Any> get() = _deleteClick

    fun deleteDataBase(context: Context) {
        context.deleteDatabase("$DATA_BASE_NAME") // This can delete Database if necessary
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

}