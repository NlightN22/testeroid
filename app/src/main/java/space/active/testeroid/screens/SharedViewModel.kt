package space.active.testeroid.screens

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users

// Owner must be Main application
class SharedViewModel: ViewModel() {
    // EditTestList -> EditTest

    private val _testForEdit = MutableLiveData<Long?>()
    val testForEdit: LiveData<Long?> get () = _testForEdit

    private val _editedUser = MutableLiveData<Users?>()
    val editedUser: LiveData<Users?> = _editedUser

    fun setTestForEdit(testId: Long){
        Log.e(TAG, "Set value testId: $testId")
        _testForEdit.value = testId
    }

    fun clearTestForEdit(){
        Log.e(TAG, "Clear value testId")
        _testForEdit.value = null
    }
    // EditTestList -> EditTest end

    fun setUserForEdit(user: Users) {
        Log.e(TAG, "fun setUserForEdit _editedUser: $user")
        _editedUser.value = user
    }

    fun clearUserForEdit(){
        Log.e(TAG, "clearUserForEdit")
        _editedUser.value = null
    }
}