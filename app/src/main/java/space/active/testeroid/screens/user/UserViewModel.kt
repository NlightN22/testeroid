package space.active.testeroid.screens.user

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.SingleLiveEvent
import space.active.testeroid.helpers.UiText
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.screens.useredit.UserEditUiState

class UserViewModel(
    // TODO add hint title when list is empty

    private val repository: DataBaseRepositoryRealization,
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    val selectedUserId: Flow<Long?> = dataStore.userId
    val selectedUserAdmin: Flow<Boolean> = dataStore.admin
    val userList: Flow<List<Users>> = repository.allUsers()

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
                viewModelScope.launch {
                    Log.e(TAG, "state.userId: ${state.user}")
                    dataStore.saveSelectedUser(state.user)
                }
            }
            is UserUiState.ShowError -> {
                viewModelScope.launch {
                    _errorMsg.emit(UiText.StringResource(state.msg))
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
        when (event) {
            is UserEvents.OnClickItem -> {
                viewModelScope.launch {
                _userForEdit = repository.getUser(event.userId)
                    val admin =  selectedUserAdmin.first()
                    if (admin) {
                        uiState(UserUiState.OpenUserEdit)
                    } else if (_userForEdit.userPassword.isNotEmpty()) {
                        uiState(UserUiState.ShowInputPasswordDialog)
                    } else if (_userForEdit.userPassword.isNullOrEmpty()) {
                        uiState(UserUiState.OpenUserEdit)
                    }
                }
            }
            is UserEvents.OnLongClickItem -> {
//                uiState(UserUiState.SelectedUser(event.userId))
            }
            is UserEvents.OkDialogPassword -> {
                if (_userForEdit.userPassword == event.password) {
                    uiState(UserUiState.OpenUserEdit)
                } else {
                    uiState(UserUiState.ShowError(R.string.user_toast_wrong_password))
                    _userForEdit = Users() // clear userForEdit
                }
            }
            is UserEvents.CancelDialogPassword -> {
                _userForEdit = Users() // clear userForEdit
            }
            is UserEvents.OnAddClick -> {
                viewModelScope.launch {
                    val admin = selectedUserAdmin.first()
                    Log.e(TAG, "OnAddClick admin:$admin")
                    if (admin) {
                        _userForEdit = Users()
                        uiState(UserUiState.OpenUserEdit)
                    } else {
                        _errorMsg.emit(UiText.StringResource(
                            R.string.user_not_admin
                        ))
                    }
                }
            }
        }
    }

    override fun onCleared() {
        Log.e(TAG, "$this is cleared")
        super.onCleared()
    }

    sealed class UserEvents {
        data class OnClickItem(val userId: Long) : UserEvents()
        data class OnLongClickItem(val userId: Long) : UserEvents()
        data class OkDialogPassword(val password: String) : UserEvents()
        object OnAddClick: UserEvents()
        object CancelDialogPassword : UserEvents()
    }
}