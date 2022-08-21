package space.active.testeroid.screens.test

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.relations.TestWithQuestions
import space.active.testeroid.helpers.UiText
import space.active.testeroid.helpers.notifyObserver
import space.active.testeroid.repository.DataStoreRepository

class TestViewModel(
    val repository: RepositoryRealization,
    val dataStore: DataStoreRepository
): ViewModel() {

    val testsWithQuestions: LiveData<List<TestWithQuestions>> =
        repository.allTestsWithQuestions()

    val correct: Flow<Int> = dataStore.correctScore
    val notCorrect: Flow<Int> = dataStore.notCorrectScore

    val selectedUser: Flow<Long?> = dataStore.userId

//    val currentList: LiveData<List<TestWithQuestions>> = _currentList

    private var _currentList: List<TestWithQuestions> = listOf()
    private lateinit var _currentTest: TestWithQuestions
    private var _count: Int = 0
    private var _size: Int = 0
    private var _score: Int = 0
//    val currentTest: LiveData<TestWithQuestions> = _currentTest

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
            is TestFormEvents.Restart -> {uiState(TestUiState.Restart(events.listTests))}
        }
    }

    private fun isCorrectAnswer(position: Int): AnswerColor {
        _currentTest?.let { test->
            if (test.questions[position].correctAnswer) {
                countingScore(true)
                return AnswerColor.Ok
            }
        }
        countingScore(false)
        return AnswerColor.NotOk
    }

    fun uiState(state: TestUiState) {
        when (state) {
            is TestUiState.ShowFirst -> {
                Log.e(TAG, "TestUiState.ShowFirst _currentList: $_currentList")
                _formState.value?.let { form-> form.restartVisibility = false }
                if (_currentList.isNullOrEmpty()) {
                    if (state.listTests.isNotEmpty()) {
                        _currentList = state.listTests
                        _count = 0
                        _currentTest = _currentList[_count]
                        _currentTest.questions = _currentTest.questions.shuffled()
                        _size = state.listTests.size
                    } else {
                        uiState(TestUiState.ShowEmpty)
                    }
                }
                if (_currentList.isNotEmpty()) {
                    Log.e(TAG, "TestUiState.ShowFirst _currentList isNotEmpty: $_currentList")
                    setForm()
                }
            }
            is TestUiState.ShowNext -> {
                _formState.value?.let { form->
                    _currentList?.let { list ->
                        if (_count >= list.lastIndex) {
                            Log.e(TAG,"Finish")
                            uiState(TestUiState.Final)
                        } else {
                            _count += 1
                            _currentTest = list[_count]
                            _currentTest.questions = _currentTest.questions.shuffled()
                            setForm()
                        }
                    }
                }
            }
            is TestUiState.ShowCorrect -> {
                _formState.value?.let { form ->
                    form.variants[state.position].correct =
                        isCorrectAnswer(position = state.position)
                    form.variants.forEach {
                        it.enabled = false
                    }
                    _formState.notifyObserver()
                    viewModelScope.launch {
                        delay(1000L)
                            uiState(TestUiState.ShowNext)
                    }
                }
            }
            is TestUiState.Restart -> {
                _score = 0
                _currentList = listOf()
                state.listTests?.let {
                    uiState(TestUiState.ShowFirst(state.listTests))
                }
            }
            is TestUiState.Final -> {
                // Show score and congratulations
                // Write score to DB user
                viewModelScope.launch {
                    val userName = selectedUser.first()?.let { userId ->
                        repository.getUser(userId).userName
                    } ?: ""
                    _formState.value?.let { form ->
                        form.title = UiText.StringResource(
                            resId = R.string.test_final_title,
                            userName,
                            _score
                        )
                        form.restartVisibility = true
                    }
                    _formState.notifyObserver()
                }
            }
            is TestUiState.ShowEmpty -> {
                _formState.value = TestFormState()
            }
        }
    }

    private fun countingScore(boolean: Boolean){
        viewModelScope.launch {
            if (boolean) {
                _score += correct.first()
            } else {
                _score -= notCorrect.first()
            }
        }
    }

    private fun setForm() {
        _formState.value?.let { form ->
            _currentTest?.let { current ->
                form.variants.forEach { variant->
                    variant.correct = AnswerColor.Neutral
                    variant.enabled = true
                }
                form.id = current.tests.testId.toString()
                form.count = (_count+1).toString()
                form.title = UiText.DynamicString(current.tests.testName)
                form.size = _size.toString()
                form.variants.forEachIndexed { index, variantState ->
                    variantState.text = current.questions[index].questionName
                }
            }
        }
        _formState.notifyObserver()
    }
}