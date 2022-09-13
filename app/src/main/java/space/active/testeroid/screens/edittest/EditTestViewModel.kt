package space.active.testeroid.screens.edittest

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.helpers.notifyObserver

class EditTestViewModel(
    private val repository: DataBaseRepositoryRealization
): ViewModel() {

//    private val _uiState = MutableLiveData<EditTestUiState>(EditTestUiState.ShowNew)
//    val uiState: LiveData<EditTestUiState> = _uiState

    private val _formState = MutableLiveData<EditTestFormState>(EditTestFormState())
    val formState: LiveData<EditTestFormState> = _formState

    private val _uiEvent = MutableSharedFlow<ValidationResult>()
    val uiEvent: SharedFlow<ValidationResult> = _uiEvent

    fun onEvent(event: EditTestFormEvents) {
        when(event) {
            is EditTestFormEvents.TitleChanged -> {
                Log.e(TAG, "TitleChanged: ${event.title}")
                _formState.value?.let { form->
                    form.title = event.title
                }
            }
            is EditTestFormEvents.Variant1 -> {_formState.value?.let { it.variant1 = event.text}}
            is EditTestFormEvents.Variant2 -> {_formState.value?.let { it.variant2 = event.text}}
            is EditTestFormEvents.Variant3 -> {_formState.value?.let { it.variant3 = event.text}}
            is EditTestFormEvents.Variant4 -> {_formState.value?.let { it.variant4 = event.text}}
            is EditTestFormEvents.CheckChanged -> {
                _formState.value?.let { form ->
                    form.listSelected[event.index] = !form.listSelected[event.index]
                }
                _formState.notifyObserver()
                Log.e(TAG, "_formState.value ${_formState.value}")
            }
            is EditTestFormEvents.Cancel -> {
            }
            is EditTestFormEvents.Submit -> {
                _formState.value?.let { form ->
                    val validate = ValidateSelected()
                    val result = validate.valid(form.listSelected)
                    viewModelScope.launch {
                        _uiEvent.emit(result)
                    }
                    if (result.successful) {
                        insertTest()
                        viewModelScope.launch {
                            uiState(state = EditTestUiState.ShowNew)
                        }
                    }
                }
            }
        }
    }

    fun uiState(state: EditTestUiState) {
        when(state) {
            is EditTestUiState.ShowNew -> {
                val newForm = EditTestFormState()
                _formState.value = newForm
                _formState.notifyObserver()
            }
            is EditTestUiState.ShowIncome -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val inputTest = repository.getTestWithQuestionsFlow(state.testId).first()
                    try {
                        _formState.value?.let { form ->
                            form.id = inputTest.tests.testId.toString()
                            form.title = inputTest.tests.testName
                            for (index in 0.. form.listSelected.lastIndex) {
                                form.listSelected[index] = inputTest.questions[index].correctAnswer
                                form.listQuestionsId[index] = inputTest.questions[index].questionId
                            }
                            form.variant1 = inputTest.questions[0].questionName
                            form.variant2 = inputTest.questions[1].questionName
                            form.variant3 = inputTest.questions[2].questionName
                            form.variant4 = inputTest.questions[3].questionName
                            Log.e(TAG, "in uiState _formState.value: ${form}")
                        }
                        _formState.notifyObserver()
                    } catch (e: Exception) {
                        Log.e(TAG, "Error in uiState EditTestUiState.ShowIncome: ${e.message}")
                    }
               }
            }
            is EditTestUiState.ShowEdited -> {}
            is EditTestUiState.Error -> {}
        }
    }

    fun insertTest () {
        _formState.value?.let { form ->
            val addId: Long = if (form.id.isNotEmpty()) {form.id.toLong()} else {0}
            val addTitle: String = form.title
            val addTest = Tests(addId, addTitle)

            val addListVariants = listOf<String>(form.variant1, form.variant2, form.variant3, form.variant4)
            val addListSelected = form.listSelected
            val addListQuestionId = form.listQuestionsId
            val addQuestions = List<Questions>(addListVariants.size) { Questions() }

            addListVariants.forEachIndexed { index, variant ->
                addQuestions[index].questionName = variant
            }
            addListSelected.forEachIndexed { index, selected ->
                addQuestions[index].correctAnswer = selected
            }
            addListQuestionId.forEachIndexed { index, id ->
                addQuestions[index].questionId = id
            }

            viewModelScope.launch {
                Log.e(TAG, "insertTest $addTest, $addQuestions")
                repository.addNewTestWithQuestions(addTest, addQuestions)
            }
        }
    }

    override fun onCleared() {
        Log.e(TAG, "EditTestViewModel override fun onCleared()")
        super.onCleared()
    }
}