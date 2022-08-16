package space.active.testeroid.screens.edittest

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.db.relations.TestWithQuestions

class EditTestViewModel(
    private val repository: RepositoryRealization
): ViewModel() {

    private val _id = MutableLiveData<Long>()
    val id: LiveData<Long> = _id
    private val _title = MutableLiveData<String>()
    val title: LiveData<String> = _title

    private val _variantList =
        MutableLiveData<ArrayList<ViewStateEditTest.Variant>>(arrayListOf()) // Important to create zero size array
    val variantList: LiveData<ArrayList<ViewStateEditTest.Variant>> = _variantList

//    private val _selectedVariants = MutableLiveData(arrayListOf<Int>())
//    val selectedVariants: LiveData<ArrayList<Int>> = _selectedVariants


    // If you use ArrayList you need to notify observer after operations with Array
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun setElementsState(viewState: ViewStateEditTest) {
        when (viewState) {
            is ViewStateEditTest.Id -> {_id.postValue(viewState.num)}
            is ViewStateEditTest.Title -> {_title.postValue(viewState.text)}
            is ViewStateEditTest.Variant -> {
                _variantList.value?.let { list ->
                    list.map {
                        if (it.position == viewState.position){
                            it.question.correctAnswer = viewState.question.correctAnswer
                            it.question.questionName = viewState.question.questionName
                        }
                    }
                    _variantList.notifyObserver()
                } ?: run {
                    Log.e(TAG, "Error fun setElementsState _variantList.value is ${_variantList.value}")
                }
            }
        }
    }

    fun setCurrentTest(testId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val test: TestWithQuestions =  repository.getTestWithQuestions(testId)
            testAdapter(test)
        }
    }

    fun setViewStateForAdd (listSize: Int){
        _variantList.value?.let {list ->
            for (i in 0 until listSize) {
                val viewState = ViewStateEditTest.Variant(position = i, Questions())
                list.add(viewState)
                setElementsState(viewState)
            }
        }?: run {
            Log.e(TAG, "Error in fun setViewStateForAdd _variantList.value: ${_variantList.value}")
        }
    }

    fun testAdapter(testWithQuestions: TestWithQuestions){
        val test = testWithQuestions.tests
        val questions = testWithQuestions.questions

        setElementsState(ViewStateEditTest.Id(test.testId))
        setElementsState(ViewStateEditTest.Title(test.testName))
        questions.forEachIndexed { index, question ->
            val viewState =
                ViewStateEditTest.Variant(
                    position = index,
                    question
                )
            _variantList.value?.let { list ->
                if (index <= 3) {
                    list.add(viewState)
                    setElementsState(viewState)
                } else {
                    Log.e (TAG, "Error in fun testAdapter _variantList.value index is ${index}")
                }
            } ?: run {
                Log.e (TAG, "Error in fun testAdapter _variantList.value is ${_variantList.value}")
            }

        }
    }

    fun  preparingDataForSending(testId: String, testName: String, textList: List<String>){
        val insertTest = Tests()
        if (testId.isNotEmpty()) {insertTest.testId = testId.toLong()}
        insertTest.testName = testName

        _variantList.value?.let { list ->
            list.forEachIndexed { index, item ->
                item.question.questionName = textList[index]
            }
            Log.e(TAG, "Add variants check to DB: $list")
            list.forEach {item ->
                Log.e(TAG, "$item")
            }
            val questions: List<Questions> = list.map { it.question }
            questions.forEach {
                Log.e(TAG, "Add variants name questionList: ${it.questionId} ${it.questionName} ${it.correctAnswer}")
            }
                    insertTestToRepository(insertTest, questions)
        }?: run {
            Log.e(TAG, "Error in fun  preparingDataForSending _variantList.value: ${_variantList.value}")
        }

    }

    fun insertTestToRepository(test: Tests, questions: List<Questions>){
        viewModelScope.launch {
            repository.addNewTestWithQuestions(test, questions)
        }
    }


    fun onClickVariantEdit(position: Int) {
        _variantList.value?.let { list ->
            val filteredList = list.filter { it.position == position }
            if (filteredList.isEmpty()) {
                val viewState = ViewStateEditTest.Variant(position = position,
                    question = Questions(correctAnswer = true))
                list.add(viewState)
                setElementsState(viewState)
            } else if (filteredList.isNotEmpty()) {
                filteredList.forEach() { item ->
                    list.map { variant ->
                        if (variant.position == item.position) {
                            variant.question.correctAnswer = !variant.question.correctAnswer
                            val viewState = ViewStateEditTest.Variant(
                                position = variant.position,
                                question = variant.question
                            )
                            setElementsState(viewState)
                        }
                    }
                }
            }
        }

//        setElementsState(ViewStateEditTest.Variant(position = position))
//        _variantList.value?.let { list ->
//            list.forEachIndexed { index, variant ->
//                if (index == position) {
//                    Log.e(TAG, "fun onClickVariantEdit $variant")
//                    variant.checked = !variant.checked
//                }
//            }
//        }?: run {
//            Log.e(TAG, "Error fun onClickVariantEdit _variantList.value is ${_variantList.value}")
//        }
//        _variantList.notifyObserver()
    }

    fun clearCorrectVariants(){
//        _selectedVariants.value?.let {
//            it.clear()
//            _selectedVariants.notifyObserver()
//        }
    }

    override fun onCleared() {
        clearCorrectVariants()
        Log.e(TAG, "Edit test VM cleared")
        super.onCleared()
    }

    sealed class ViewStateEditTest{
        data class Id (val num: Long): ViewStateEditTest()
        data class Title (var text: String): ViewStateEditTest()
        data class Variant (var position: Int,
                            var question: Questions): ViewStateEditTest()
    }
}