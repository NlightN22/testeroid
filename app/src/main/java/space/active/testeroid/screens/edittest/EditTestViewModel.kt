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

    private val selectedCorrectVariantsMutable = MutableLiveData(arrayListOf<Int>())
    val selectedCorrectVariants: LiveData<ArrayList<Int>> = selectedCorrectVariantsMutable


    // If you use ArrayList you need to notify observer after operations with Array
    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun setElementsState(viewState: ViewStateEditTest) {
        when (viewState) {
            is ViewStateEditTest.Id -> {_id.postValue(viewState.num)}
            is ViewStateEditTest.Title -> {_title.postValue(viewState.text)}
            is ViewStateEditTest.Variant -> {
                _variantList.value?.let {
                    it.add(viewState)
                } ?: run {
                    Log.e(TAG, "Error fun setElementsState _variantList.value is ${_variantList.value}")
                }
                _variantList.notifyObserver()
            }
        }
    }

    fun setCurrentTest(testId: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            val test: TestWithQuestions =  repository.getTestWithQuestions(testId)
            testAdapter(test)
        }
    }

    fun testAdapter(testWithQuestions: TestWithQuestions){
        val test = testWithQuestions.tests
        val questions = testWithQuestions.questions

        setElementsState(ViewStateEditTest.Id(test.testId))
        setElementsState(ViewStateEditTest.Title(test.testName))
        questions.forEachIndexed { index, question ->
            setElementsState(
                ViewStateEditTest.Variant(
                    position = index,
                    question.questionName,
                    question.correctAnswer
                )
            )
        }

    }

    fun addNewTestWithQuestions(test: Tests, questions: List<Questions>){
        viewModelScope.launch {
            repository.addNewTestWithQuestions(test, questions)
        }
    }

    fun sendDataToRepository(test: String) {

    }

    fun onClickVariantEdit(position: Int) {
        _variantList.value?.let { list ->
            list.forEachIndexed { index, variant ->
                if (index == position) {
                    Log.e(TAG, "fun onClickVariantEdit $variant")
                    variant.checked = !variant.checked
                }
            }
        }?: run {
            Log.e(TAG, "Error fun onClickVariantEdit _variantList.value is ${_variantList.value}")
        }
        _variantList.notifyObserver()
    }

    fun clearCorrectVariants(){
        selectedCorrectVariantsMutable.value?.let {
            it.clear()
            selectedCorrectVariantsMutable.notifyObserver()
        }
    }

    override fun onCleared() {
        clearCorrectVariants()
        Log.e(TAG, "Edit test VM cleared")
        super.onCleared()
    }

    sealed class ViewStateEditTest{
        data class Id (val num: Long): ViewStateEditTest()
        data class Title (var text: String): ViewStateEditTest()
        data class Variant (var position: Int, var text: String, var checked: Boolean): ViewStateEditTest()
    }
}