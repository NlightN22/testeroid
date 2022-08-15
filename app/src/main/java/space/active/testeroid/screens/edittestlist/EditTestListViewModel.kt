package space.active.testeroid.screens.edittestlist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.modelsdb.Tests

class EditTestListViewModel(private val repository: RepositoryRealization): ViewModel() {

    private val _testForEdit = MutableLiveData<Long?>()
    val testForEdit: LiveData<Long?> get () = _testForEdit

    private val _selectedTestsList = MutableLiveData<ArrayList<Tests>>(arrayListOf()) // Important to create zero size array
    val selectedTestsList: LiveData<ArrayList<Tests>> get() = _selectedTestsList

    val allTests: LiveData<List<Tests>> = repository.getAllTests()


    // If you use ArrayList you need to notify observer after operations with Array
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    init {
        Log.e(TAG, "EditTestViewModel created")
    }

    fun setTestForEdit(testId: Long){
        Log.e(TAG, "Set value: $testId")
        _testForEdit.value = testId
    }

    fun clearTestForEdit(){
        Log.e(TAG, "clearTestForEdit")
        _testForEdit.value = null
    }

    fun selectListItem(testId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val currentTest: Tests = repository.getTest(testId)
            _selectedTestsList.value?.let { listSelectedTest ->
                if (listSelectedTest.contains(currentTest)) {
                    listSelectedTest.remove(currentTest)
                } else {
                    listSelectedTest.add(currentTest)
                }
                _selectedTestsList.notifyObserver()
            } ?: run {
                Log.e(TAG, "Error fun selectListItem _selectedTests.value is ${_selectedTestsList.value}")
            }
        }
    }

    fun deleteTestsWithQuestions(){
        viewModelScope.launch(Dispatchers.IO) {
            _selectedTestsList.value?.forEach {
                repository.deleteTestWithQuestions(it.testId)
            }
            _selectedTestsList.value?.clear()
            _selectedTestsList.notifyObserver()
//            Log.e(TAG,"After Clear ${selectedTests.value}")
        }
//        Log.e(TAG,"After Delete ${selectedTests.value}")
    }

    override fun onCleared() {
        Log.e(TAG, "EditTestListViewModel cleared")
        super.onCleared()
    }

}