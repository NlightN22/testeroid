package space.active.testeroid.screens.edittest

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.screens.edittestlist.EditTestListViewModel

class EditTestViewModel(
    private val repository: RepositoryRealization
): ViewModel() {

    private val _uiState = MutableLiveData<EditTestUiState>(EditTestUiState.ShowNew)
    val uiState: LiveData<EditTestUiState> = _uiState

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

        // If you use ArrayList you need to notify observer after operations with Array
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    sealed class EditTestUiState {
        object ShowNew : EditTestUiState()
        data class ShowIncome (val testId: Long): EditTestUiState()
        object ShowEdited: EditTestUiState()
        data class Error (val message: Int): EditTestUiState()
    }

    override fun onCleared() {
        Log.e(TAG, "EditTestViewModel override fun onCleared()")
        super.onCleared()
    }

    //    private val _id = MutableLiveData<Long>()
//    val id: LiveData<Long> = _id
//    private val _title = MutableLiveData<String>()
//    val title: LiveData<String> = _title
//
//    private val _variantList =
//        MutableLiveData<ArrayList<ViewStateEditTest.Variant>>(arrayListOf()) // Important to create zero size array
//    val variantList: LiveData<ArrayList<ViewStateEditTest.Variant>> = _variantList
//
//    // If you use ArrayList you need to notify observer after operations with Array
//    fun <T> MutableLiveData<T>.notifyObserver() {
//        this.postValue(this.value)
//    }
//
//    fun setElementsState(viewState: ViewStateEditTest) {
//        when (viewState) {
//            is ViewStateEditTest.Id -> {_id.postValue(viewState.num)}
//            is ViewStateEditTest.Title -> {_title.postValue(viewState.text)}
//            is ViewStateEditTest.Variant -> {
//                _variantList.value?.let { list ->
//                    list.map {
//                        if (it.position == viewState.position){
//                            it.question.correctAnswer = viewState.question.correctAnswer
//                            it.question.questionName = viewState.question.questionName
//                        }
//                    }
//                    _variantList.notifyObserver()
//                } ?: run {
//                    Log.e(TAG, "Error fun setElementsState _variantList.value is ${_variantList.value}")
//                }
//            }
//        }
//    }
//
//    fun setCurrentTest(testId: Long) {
//        viewModelScope.launch(Dispatchers.IO) {
//            val test: TestWithQuestions =  repository.getTestWithQuestions(testId)
//            testAdapter(test)
//        }
//    }
//
//    fun setViewStateForAdd (listSize: Int){
//        _variantList.value?.let {varList ->
//            if (varList.size < listSize) {
//                for (i in 0 until listSize) {
//                    val viewState = ViewStateEditTest.Variant(position = i, Questions())
//                    varList.add(viewState)
//                    setElementsState(viewState)
//                }
//            }
//        }?: run {
//            Log.e(TAG, "Error in fun setViewStateForAdd _variantList.value: ${_variantList.value}")
//        }
//    }
//
//    fun testAdapter(testWithQuestions: TestWithQuestions){
//        val test = testWithQuestions.tests
//        val questions = testWithQuestions.questions
//
//        setElementsState(ViewStateEditTest.Id(test.testId))
//        setElementsState(ViewStateEditTest.Title(test.testName))
//        questions.forEachIndexed { index, question ->
//            val viewState =
//                ViewStateEditTest.Variant(
//                    position = index,
//                    question
//                )
//            _variantList.value?.let { list ->
//                if (index <= 3) {
//                    list.add(viewState)
//                    setElementsState(viewState)
//                } else {
//                    Log.e (TAG, "Error in fun testAdapter _variantList.value index is ${index}")
//                }
//            } ?: run {
//                Log.e (TAG, "Error in fun testAdapter _variantList.value is ${_variantList.value}")
//            }
//        }
//    }
//
//    fun setTextToVariants(index: Int, text: String){
////        Log.e(TAG, "text: $text")
//        _variantList.value?.let { list ->
//            list[index].question.questionName = text
//        }
//    }
//
//    fun  preparingDataForSending(testId: String, testName: String, textList: List<String>){
//        //prepare test
//        val insertTest = Tests()
//        if (testId.isNotEmpty()) {insertTest.testId = testId.toLong()}
//        insertTest.testName = testName
//
//        //prepare questions
//        _variantList.value?.let { list ->
//            list.forEachIndexed { index, item ->
//                item.question.questionName = textList[index]
//            }
//            Log.e(TAG, "Add variants check to DB: $list")
//            list.forEach {item ->
//                Log.e(TAG, "$item")
//            }
//            val questions: List<Questions> = list.map { it.question }
//            questions.forEach {
//                Log.e(TAG, "Add variants name questionList: ${it.questionId} ${it.questionName} ${it.correctAnswer}")
//            }
//            insertTestToRepository(insertTest, questions)
//        }?: run {
//            Log.e(TAG, "Error in fun  preparingDataForSending _variantList.value: ${_variantList.value}")
//        }
//    }
//
//    fun insertTestToRepository(test: Tests, questions: List<Questions>){
//        viewModelScope.launch {
//            repository.addNewTestWithQuestions(test, questions)
//        }
//    }
//
//    fun setCorrectAnswer(position: Int) {
//        _variantList.value?.let { list ->
//            val filteredList = list.filter { it.position == position }
//            if (filteredList.isEmpty()) {
//                val viewState = ViewStateEditTest.Variant(position = position,
//                    question = Questions(correctAnswer = true))
//                setElementsState(viewState)
//            } else if (filteredList.isNotEmpty()) {
//                filteredList.forEach() { item ->
//                    list.map { variant ->
//                        if (variant.position == item.position) {
//                            variant.question.correctAnswer = !variant.question.correctAnswer
//                            val viewState = ViewStateEditTest.Variant(
//                                position = variant.position,
//                                question = variant.question
//                            )
//                            setElementsState(viewState)
//                        }
//                    }
//                }
//            }
//        }
//    }
//
//    fun clearCorrectVariants(){
//        _variantList.value?.let { list->
//            list.map { it.question.correctAnswer = false }
//        }
//        _variantList.notifyObserver()
//    }
//
//    override fun onCleared() {
//        clearCorrectVariants()
//        Log.e(TAG, "Edit test VM cleared")
//        super.onCleared()
//    }
//
//    sealed class ViewStateEditTest{
//        data class Id (val num: Long): ViewStateEditTest()
//        data class Title (var text: String): ViewStateEditTest()
//        data class Variant (var position: Int,
//                            var question: Questions): ViewStateEditTest()
//    }
}