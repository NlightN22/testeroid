package space.active.testeroid.screens.user

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.SingleLiveEvent
import space.active.testeroid.helpers.UiText
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.RepositoryRealization

class UserViewModel(
    private val repository: RepositoryRealization,
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    val selectedUser: Flow<Long?> = dataStore.userId
    val userList: LiveData<List<Users>> = repository.allUsers()

    private val _passwordDialogEvent = SingleLiveEvent<String>()
    val passwordDialogEvent: LiveData<String> = _passwordDialogEvent

    private val _openEditUserEvent = SingleLiveEvent<Users>()
    val openEditUserEvent: LiveData<Users> = _openEditUserEvent

    private val _errorMsg = MutableSharedFlow<UiText>()
    val errorMsg: SharedFlow<UiText> = _errorMsg

    private var _userForEdit = Users()

    private fun uiState(state: UserUiState) {
        when (state) {
            is UserUiState.SelectedUser -> {
                viewModelScope.launch(Dispatchers.IO) {
                    dataStore.saveUserId(state.userId)
                }
            }
            is UserUiState.ShowError -> {
                viewModelScope.launch {
                    _errorMsg.emit(UiText.DynamicString(state.msg))
                }
            }
            is UserUiState.ShowInputPasswordDialog -> {
                _passwordDialogEvent.postValue(_userForEdit.userName)
            }
            is UserUiState.OpenUserEdit -> {

                // call open new Fragment
                _openEditUserEvent.value = _userForEdit
                // clear _userforedit
                _userForEdit = Users() // clear userForEdit
            }
        }
    }

    fun onEvent(event: UserEvents) {
        when (event){
            is UserEvents.OnClickItem -> {
                viewModelScope.launch {
                    _userForEdit = repository.getUser(event.userId)
                    if (_userForEdit.userPassword.isNotEmpty()) {
                        uiState(UserUiState.ShowInputPasswordDialog)
                    } else {
                        uiState(UserUiState.OpenUserEdit)
                    }
                }
            }
            is UserEvents.OnLongClickItem -> {
                uiState(UserUiState.SelectedUser(event.userId))
            }
            is UserEvents.OkDialogPassword -> {
                if (_userForEdit.userPassword == event.password) {
                    uiState(UserUiState.OpenUserEdit)
                } else {
                    uiState(UserUiState.ShowError("Wrong password")) // TODO replace for R string
                    _userForEdit = Users() // clear userForEdit
                }
            }
            is UserEvents.CancelDialogPassword -> {
                _userForEdit = Users() // clear userForEdit
            }
        }
    }

//    private val _userForEdit = MutableLiveData<Users>()
//    val userForEdit: LiveData<Users> = _userForEdit

//    private val _passwordCheckResult = SingleLiveEvent<CheckState>()
//    val passwordCheckResult: LiveData<CheckState> = _passwordCheckResult

//    fun setUserForEdit(userId: Long){
//        // Coroutines with start another fragment or activity not work.
//        // Need to prepare values for sending
//        viewModelScope.launch(Dispatchers.IO) {
//            Log.e(TAG, "setUserForEdit: $userId")
//            val user = repository.getUser(userId)
//            Log.e(TAG, "setUserForEdit user repository.getUser(userId): $user")
//            _userForEdit.postValue(user)
//            Log.e(TAG, "setUserForEdit postValue: ${_userForEdit.value}")
//            if (user.userPassword.isNotEmpty()) {
//                _passwordDialogEvent.postValue(user.userName)
//                _passwordCheckResult.postValue(CheckState.NeedPassword)
//            }else if (user.userPassword.isEmpty()) {
//                _passwordCheckResult.postValue(CheckState.Ok)
//            }
//        }
//    }

//    fun checkUserPassword(password: String){
//        Log.e(TAG, "fun checkUserPassword $password")
//        if (_userForEdit.userPassword == password) {
//            _passwordCheckResult.value = CheckState.Ok
//        } else {
//            _passwordCheckResult.value = CheckState.NotOk
//        }
//    }

//    fun clearUserForEdit(){
//        _userForEdit.value = Users() // clean user
//    }

    override fun onCleared() {
        Log.e(TAG, "$this is cleared")
//        Log.e(TAG, "_userForEdit.value ${_userForEdit.value}")
        super.onCleared()
    }

    sealed class UserEvents {
        data class OnClickItem(val userId: Long): UserEvents()
        data class OnLongClickItem(val userId: Long): UserEvents()
        data class OkDialogPassword(val password: String): UserEvents()
        object CancelDialogPassword: UserEvents()
    }

enum class CheckState{Ok, NotOk, NeedPassword}
}