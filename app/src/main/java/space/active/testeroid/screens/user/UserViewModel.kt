package space.active.testeroid.screens.user

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.SingleLiveEvent
import space.active.testeroid.helpers.UiText
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.screens.use_cases.SelectUser

class UserViewModel(
    // TODO add hint title when list is empty

    private val repository: DataBaseRepositoryRealization,
    private val dataStore: DataStoreRepository
) : ViewModel() {

    val selectedUserId: Flow<Long?> = dataStore.userId
    val selectedUserAdmin: Flow<Boolean> = dataStore.admin
    val userList: Flow<List<Users>> = repository.allUsers()

    private val _passwordDialogEvent = SingleLiveEvent<String>()
    val passwordDialogEvent: LiveData<String> = _passwordDialogEvent

    private val _openEditUserEvent = SingleLiveEvent<Users>()
    val openEditUserEvent: LiveData<Users> = _openEditUserEvent

    private val _errorMsg = MutableSharedFlow<UiText>()
    val errorMsg: SharedFlow<UiText> = _errorMsg

    private var _passwordReason = PasswordReason.ToEdit

    private var _userForEdit = Users()

    private fun uiState(state: UserUiState) {
        when (state) {
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
                _userForEdit = Users() // clear userForEdit
            }
        }
    }

    fun onEvent(event: UserEvents) {
        when (event) {
            is UserEvents.OnCheckBoxClick -> {
                viewModelScope.launch {
                    _userForEdit = repository.getUser(event.userId)
                    val checkedAdmin = selectedUserAdmin.first()
                    if (checkUserPasswordOrAdmin(_userForEdit, checkedAdmin)) {
                        selectUser(_userForEdit.userId)
                    } else  {
                        _passwordReason = PasswordReason.ToSelect
                        uiState(UserUiState.ShowInputPasswordDialog)
                    }


                }
            }
            is UserEvents.OnClickItem -> {
                viewModelScope.launch {
                    val checkedUser = repository.getUser(event.userId)
                    val checkedAdmin = selectedUserAdmin.first()
                    if (checkUserPasswordOrAdmin(checkedUser, checkedAdmin)) {
                        _userForEdit = checkedUser
                        uiState(UserUiState.OpenUserEdit)
                    } else  {
                        _passwordReason = PasswordReason.ToEdit
                        uiState(UserUiState.ShowInputPasswordDialog)
                    }
                }
            }
            is UserEvents.OnLongClickItem -> {
            }
            is UserEvents.OkDialogPassword -> {
                if (_userForEdit.userPassword == event.password) {
                    if (_passwordReason == PasswordReason.ToEdit) {
                        uiState(UserUiState.OpenUserEdit)
                    } else if (_passwordReason == PasswordReason.ToSelect) {
                        selectUser(_userForEdit.userId)
                    }
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
                        _errorMsg.emit(
                            UiText.StringResource(
                                R.string.user_not_admin
                            )
                        )
                    }
                }
            }
        }
    }

    private fun selectUser(userId: Long) {
        viewModelScope.launch {
            val result = SelectUser(repository, dataStore).invoke(userId)
            if (result.userName.isNotBlank()) {
                _errorMsg.emit(
                    UiText.StringResource(
                        R.string.edit_user_msg_select, result.userName
                    )
                )
                _userForEdit = Users()
            }
        }
    }

    private fun checkUserPasswordOrAdmin(user: Users, selectUserAdmin: Boolean): Boolean {
        if (selectUserAdmin || user.userPassword.isBlank()) {
            return true
        }
        return false
    }

    override fun onCleared() {
        Log.e(TAG, "$this is cleared")
        super.onCleared()
    }


}