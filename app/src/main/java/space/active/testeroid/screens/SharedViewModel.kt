package space.active.testeroid.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.active.testeroid.TAG

// Owner must be Main application
class SharedViewModel: ViewModel() {
    // EditTestList -> EditTest
    private val _testForEdit = MutableLiveData<Long?>()
    val testForEdit: LiveData<Long?> get () = _testForEdit

    fun setTestForEdit(testId: Long){
        Log.e(TAG, "Set value: $testId")
        _testForEdit.value = testId
    }

    fun clearTestForEdit(){
        Log.e(TAG, "clearTestForEdit")
        _testForEdit.value = null
    }
    // EditTestList -> EditTest end
}