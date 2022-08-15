package space.active.testeroid.screens.test

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.relations.TestWithQuestions

class TestViewModel(
    private val repository: RepositoryRealization
): ViewModel() {

    private val _currentTest = MutableLiveData<TestWithQuestions>()
    val currentTest: LiveData<TestWithQuestions> = _currentTest


    val testsWithQuestions: LiveData<List<TestWithQuestions>> =
        repository.allTestsWithQuestions()

    val testsSize: LiveData<Int> =
        Transformations.map(testsWithQuestions) {
        it.size
        }

    private val _currentTestIndex = MutableLiveData<Int>(1)
    val currentTestIndex: LiveData<Int> = _currentTestIndex

    val emptyTest = TestWithQuestions (Tests(),listOf(Questions(),Questions(),Questions(),Questions()))

    init {
        setNextTest(listOf(), emptyTest)
    }

    fun setNextTest(list: List<TestWithQuestions>,
                    currentTest: TestWithQuestions)
    {
        if (list.isNotEmpty()){
            val currentIndex = list.indexOf(currentTest)
            if (currentIndex < list.lastIndex){
                _currentTest.value = list[currentIndex+1]
                _currentTestIndex.value = currentIndex + 2
            }else {
                _currentTest.value = currentTest
                _currentTestIndex.value = currentIndex + 1
            }

        }else if (list.isEmpty()) {
            _currentTest.value = emptyTest
            _currentTestIndex.value = 1
        }
        Log.e(TAG, "setNextTest ${_currentTest.value?.tests?.testId} ")
    }

    fun setCurrentTest(currentTest: TestWithQuestions) {
        _currentTest.value = currentTest
    }

}