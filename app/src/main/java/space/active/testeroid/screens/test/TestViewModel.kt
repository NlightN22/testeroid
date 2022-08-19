package space.active.testeroid.screens.test

import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.relations.TestWithQuestions

class TestViewModel(
    repository: RepositoryRealization
): ViewModel() {

    val testsWithQuestions: LiveData<List<TestWithQuestions>> =
        repository.allTestsWithQuestions()

    private val _currentList = MutableLiveData<List<TestWithQuestions>>()
    val currentList: LiveData<List<TestWithQuestions>> = _currentList

    private val _currentTest = MutableLiveData<TestWithQuestions>()
    val currentTest: LiveData<TestWithQuestions> = _currentTest

    private val _formState = MutableLiveData<TestFormState>(TestFormState())
    val formState: LiveData<TestFormState> = _formState

    private val _ui = MutableLiveData<TestUiState>()
    val ui: LiveData<TestUiState> = _ui

    fun onEvent(events: TestFormEvents){
        when(events){
            is TestFormEvents.Variant1 -> {uiState(TestUiState.ShowCorrect(0))}
            is TestFormEvents.Variant2 -> {uiState(TestUiState.ShowCorrect(1))}
            is TestFormEvents.Variant3 -> {uiState(TestUiState.ShowCorrect(2))}
            is TestFormEvents.Variant4 -> {uiState(TestUiState.ShowCorrect(3))}
        }
    }

    private fun isCorrectAnswer(position: Int): AnswerColor {
        _currentTest.value?.let { test->
            if (test.questions[position].correctAnswer) {
                return AnswerColor.Ok
            }
        }
        return AnswerColor.NotOk
    }

    fun uiState(state: TestUiState) {
        when (state) {
            is TestUiState.ShowFirst -> {
                if (state.listTests.isNotEmpty()) {
                    _currentList.value = state.listTests
                    val firstTest = state.listTests[0]
                    _currentTest.value = firstTest
                    val size: Int = state.listTests.size
                    val count = 0
                    setForm(size, count)
                } else {
                    uiState(TestUiState.ShowEmpty)
                }
            }
            is TestUiState.ShowNext -> {
                _formState.value?.let { form->
                    form.correctList.fill(AnswerColor.Neutral)
                    _currentList.value?.let { list ->
                        val countNull = form.count.toIntOrNull()
                        countNull?.let { count ->
                            if (count == list.lastIndex) {
                                uiState(TestUiState.Final)
                            } else {
                                val index = count + 1
                                _currentTest.value = list[index]
                                setForm(list.size, index)

                            }
                        }
                    }
                    _formState.notifyObserver()
                }
            }
            is TestUiState.ShowCorrect -> {
                _formState.value?.let { form->
                    form.correctList[state.position] = isCorrectAnswer(state.position)
                    _formState.notifyObserver()
                }
                viewModelScope.launch {
                    delay(1000L)
                    uiState(TestUiState.ShowNext)
                }
            }
            is TestUiState.Final -> {}
            is TestUiState.ShowEmpty -> {
                _formState.value = TestFormState()
            }
        }
    }

    private fun setForm(size: Int, count: Int) {
        _formState.value?.let { form ->
            _currentTest.value?.let { current ->
                form.id = current.tests.testId.toString()
                form.count = count.toString()
                form.title = current.tests.testName
                form.size = size.toString()
                form.variant1 = current.questions[0].questionName
                form.variant2 = current.questions[1].questionName
                form.variant3 = current.questions[2].questionName
                form.variant4 = current.questions[3].questionName
            }
        }
        _formState.notifyObserver()
    }

    private fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }
}