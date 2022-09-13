package space.active.testeroid.screens.edittestlist

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.helpers.notifyObserver
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.screens.edittest.EditTestFragment

class EditTestListViewModel(
    private val repository: DataBaseRepositoryRealization,
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    private val _testForEdit = MutableSharedFlow<Long>()
    val testForEdit: SharedFlow<Long> = _testForEdit

    private val _selectedTestsList = MutableLiveData<ArrayList<Tests>>(arrayListOf()) // Important to create zero size array
    val selectedTestsList: LiveData<ArrayList<Tests>> get() = _selectedTestsList

    val allTests: LiveData<List<Tests>> = repository.getAllTests()

    private val _newModalFragment = MutableSharedFlow<Fragment>()
    val newModalFragment: SharedFlow<Fragment> = _newModalFragment

    private val _formState = MutableStateFlow<EditTestListUiState>(EditTestListUiState.NotSelectedItem)
    val formState: SharedFlow<EditTestListUiState> = _formState

    init {
        Log.e(TAG, "EditTestViewModel created")
    }

    fun onEvent(event: EditTestListEvents) {
        viewModelScope.launch {
            val admin: Boolean = dataStore.admin.first()
            if (admin) {
                when (event) {
                    is EditTestListEvents.OnItemClick -> {
                        val selected =  _formState.first()
                        if (selected == EditTestListUiState.NotSelectedItem) {
                            _testForEdit.emit(event.itemId)
                        }
                    }
                    is EditTestListEvents.OnItemLongClick -> {
                        val currentTest: Tests = repository.getTest(event.itemId)
                        _selectedTestsList.value?.let { listSelectedTest ->
                            if (listSelectedTest.contains(currentTest)) {
                                listSelectedTest.remove(currentTest)
                            } else {
                                listSelectedTest.add(currentTest)
                            }
                            if (listSelectedTest.isEmpty()) {
                                uiState(EditTestListUiState.NotSelectedItem)
                            } else {
                                uiState(EditTestListUiState.SelectedItem)
                            }
                            _selectedTestsList.notifyObserver()
                        } ?: run {
                            Log.e(TAG, "Error fun selectListItem _selectedTests.value is ${_selectedTestsList.value}")
                        }
                    }
                    is EditTestListEvents.OnAddClick -> {
                        // check for admin if it is open add Test
                        _newModalFragment.emit(EditTestFragment())
                    }
                    is EditTestListEvents.OnDeleteClick -> {
                        viewModelScope.launch(Dispatchers.IO) {
                            _selectedTestsList.value?.forEach {
                                repository.deleteTestWithQuestions(it.testId)
                            }
                            _selectedTestsList.value?.clear()
                            _selectedTestsList.notifyObserver()
                        }
                    }
                }
            }
        }
    }

    fun uiState(state: EditTestListUiState) {
        when (state) {
            is EditTestListUiState.SelectedItem -> {
                viewModelScope.launch {
                    _formState.emit(EditTestListUiState.SelectedItem)
                }
            }
            is EditTestListUiState.NotSelectedItem -> {
                viewModelScope.launch {
                    _formState.emit(EditTestListUiState.NotSelectedItem)
                }
            }
        }
    }

    override fun onCleared() {
        Log.e(TAG, "EditTestListViewModel cleared")
        super.onCleared()
    }
}