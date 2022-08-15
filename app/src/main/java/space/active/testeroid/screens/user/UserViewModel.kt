package space.active.testeroid.screens.user

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.SingleLiveEvent
import space.active.testeroid.repository.RepositoryRealization

class UserViewModel(private val repository: RepositoryRealization): ViewModel() {

    val userList: LiveData<List<Users>> = repository.allUsers()

    private val _selectedUser = MutableLiveData<ArrayList<Users>>(arrayListOf())
    val selectedUser: LiveData<ArrayList<Users>> = _selectedUser

    private val _userForEdit = MutableLiveData<Users>()
    val userForEdit: LiveData<Users> = _userForEdit

    private val _passwordDialogEvent = SingleLiveEvent<String>()
    val passwordDialogEvent: LiveData<String> = _passwordDialogEvent

    private val _passwordCheckResult = SingleLiveEvent<CheckState>()
    val passwordCheckResult: LiveData<CheckState> = _passwordCheckResult

    fun <T> MutableLiveData<T>.notifyObserver() {
        this.postValue(this.value)
    }

    fun setUserForEdit(userId: Long){
        // Coroutines with start another fragment or activity not work.
        // Need to prepare values for sending
        viewModelScope.launch(Dispatchers.IO) {
            Log.e(TAG, "setUserForEdit: $userId")
            val user = repository.getUser(userId)
            Log.e(TAG, "setUserForEdit user repository.getUser(userId): $user")
            _userForEdit.postValue(user)
            Log.e(TAG, "setUserForEdit postValue: ${_userForEdit.value}")
            if (user.userPassword.isNotEmpty()) {
                _passwordDialogEvent.postValue(user.userName)
                _passwordCheckResult.postValue(CheckState.needPassword)
            }else if (user.userPassword.isEmpty()) {
                _passwordCheckResult.postValue(CheckState.ok)
            }
        }
    }

    fun checkUserPassword(password: String){
        Log.e(TAG, "fun checkUserPassword $password")
        _userForEdit.value?.let { user ->
            if (user.userPassword == password) {
                _passwordCheckResult.value = CheckState.ok
            } else {
                _passwordCheckResult.value = CheckState.notOk
            }
        }
    }

    fun selectUserListItem(userId: Long){
        viewModelScope.launch(Dispatchers.IO) {
            val user = repository.getUser(userId)
            _selectedUser.value?.let { list ->
                if (list.contains(user)) {
                    list.remove(user)
                } else {
                    list.add(user)
                }
                _selectedUser.notifyObserver()
            }?: run {
                Log.e(TAG, "Error fun selectUserListItem _selectedUser.value is ${_selectedUser.value}")
            }
        }
    }

    fun clearUserForEdit(){
        _userForEdit.value = Users() // clean user
    }

    override fun onCleared() {
        Log.e(TAG, "$this is cleared")
        Log.e(TAG, "_userForEdit.value ${_userForEdit.value}")
        super.onCleared()
    }

enum class CheckState{ok, notOk, needPassword}
enum class EditState {Edit, Add}

}