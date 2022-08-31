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

    private val _editedUser = MutableLiveData<Long?>()
    val editedUser: LiveData<Long?> = _editedUser

    fun setTestForEdit(testId: Long){
        Log.e(TAG, "Set value testId: $testId")
        _testForEdit.value = testId
    }

    fun clearTestForEdit(){
        Log.e(TAG, "Clear value testId")
        _testForEdit.value = null
    }
    // EditTestList -> EditTest end

    fun setUserForEdit(userId: Long) {
        Log.e(TAG, "fun setUserForEdit _editedUser: $userId")
        _editedUser.value = userId
    }

    fun clearUserForEdit(){
        Log.e(TAG, "Clear value _editedUser")
        _editedUser.value = null
    }


}