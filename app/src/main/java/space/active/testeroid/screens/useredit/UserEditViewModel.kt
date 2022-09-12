package space.active.testeroid.screens.useredit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.UiText
import space.active.testeroid.helpers.notifyObserver
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.Repository

class UserEditViewModel(
    private val repository: Repository,
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    private var _editedUser: Users = Users()

    private val _formState = MutableLiveData<UserEditFormState>(UserEditFormState())
    val formState: LiveData<UserEditFormState> = _formState

    private var _activeForm = false

    private val _terminateSignal = MutableSharedFlow<Boolean>()
    val terminateSignal: SharedFlow<Boolean> = _terminateSignal

    private val _errorMsg = MutableSharedFlow<UiText>()
    val errorMsg: SharedFlow<UiText> = _errorMsg

    private val _adminList: Flow<List<Users>> = repository.allUsers()

    // TODO add admin rights to edit another users. Lastadmin control. Create admin by admin

    fun uiState (state: UserEditUiState) {
        when (state) {
            is UserEditUiState.NewUser -> {
                _activeForm = true
                _formState.value?.let { form->
                    form.deleteEnabled = false
                    form.selectedEnabled = false
                }
                _formState.notifyObserver()
            }
            is UserEditUiState.EditUser -> {
                _activeForm = true
                _formState.value?.let { form ->
                    form.id = _editedUser.userId.toString()
                    form.username = _editedUser.userName
                    form.password = _editedUser.userPassword
                    form.administrator = _editedUser.userAdministrator
                    form.deleteEnabled = true
                    form.selectedEnabled = true
                }
                _formState.notifyObserver()
            }
            is UserEditUiState.RestoreForm -> {
                _formState.value?.let { form ->
                    form.id = _editedUser.userId.toString()
                    form.username = _editedUser.userName
                    form.password = _editedUser.userPassword
                    form.administrator = _editedUser.userAdministrator
                }
                _formState.notifyObserver()
            }
            is UserEditUiState.LastAdmin -> {
                _formState.value?.let { form->
                    Log.e(TAG, "UserEditUiState.LastAdmin")
                    form.adminEnabled = false
                    form.administrator = true
                    _formState.notifyObserver()
                }
            }
            is UserEditUiState.ShowError -> {
                viewModelScope.launch {
                    _errorMsg.emit(state.uiText)
                }
            }
        }
    }

    fun onEvent (event: UserEditEvents) {
        when (event) {
            is UserEditEvents.OpenFragment -> {
                _formState.value?.let { form ->
                    if (!_activeForm) {
                        event.userId?.let {
                            viewModelScope.launch {
                                _editedUser = repository.getUser(event.userId)
                                uiState(UserEditUiState.EditUser)
                                // Check For Last Admin
                                _adminList.collectLatest { list->
                                    val filtered = list.filter { it.userAdministrator }
                                    if (filtered.size == 1 && filtered.any {users ->
                                            users.userName == _editedUser.userName
                                        })
                                    {
                                        Log.e(TAG, "_adminList $filtered")
                                        uiState(UserEditUiState.LastAdmin)
                                    }
                                }
                            }
                        }?: run {
                            _editedUser = Users()
                            uiState(UserEditUiState.NewUser)
                            // Check For Last Admin
                            viewModelScope.launch {
                                _adminList.collectLatest { list ->
                                    val filtered = list.filter { it.userAdministrator }
                                    if (filtered.isEmpty()) {
                                        uiState(UserEditUiState.LastAdmin)
                                    }
                                }
                            }
                        }
                    } else {
                        uiState(UserEditUiState.RestoreForm)
                    }

                }
            }
            is UserEditEvents.OnAdminCheckboxClick -> {
                _formState.value?.let {form ->
                    form.administrator = !form.administrator
                    _editedUser.userAdministrator = form.administrator
                }
                _formState.notifyObserver()
            }
            is UserEditEvents.OnOkClick -> {
                if (validateForm()) {
                    viewModelScope.launch {
                        repository.addUser(_editedUser)
                        _terminateSignal.emit(true)
                    }
                }
            }
            is UserEditEvents.OnCancelClick -> {
                viewModelScope.launch {
                    _terminateSignal.emit(true)
                }
            }
            is UserEditEvents.OnDeleteClick -> {
                viewModelScope.launch {
                    repository.deleteUser(_editedUser)
                    _terminateSignal.emit(true)
                }
            }
            is UserEditEvents.OnSelectClick -> {
                viewModelScope.launch {
                    dataStore.saveUserId(_editedUser.userId)
                    uiState(UserEditUiState.ShowError(UiText.StringResource(
                            R.string.edit_user_msg_select, _editedUser.userName
                        )))
                }
            }
            is UserEditEvents.OnEditUsername -> {
                _editedUser.userName = event.string
            }
            is UserEditEvents.OnEditPassword -> {
                _editedUser.userPassword = event.string
            }
        }
    }

    private fun validateForm(): Boolean {
            if (_editedUser.userName.isNotEmpty()) {
                if (!_editedUser.userAdministrator) {
                    return true
                } else if (_editedUser.userAdministrator) {
                    if (_editedUser.userPassword.isNotEmpty()) {
                        return true
                    } else {
                        uiState(UserEditUiState.ShowError(UiText.StringResource(
                            R.string.edit_user_empty_password
                        )))
                    }
                }
            } else {
                uiState(UserEditUiState.ShowError(UiText.StringResource(
                    R.string.edit_user_empty_username
                )))
            }
        return false
    }

    override fun onCleared() {
        _editedUser = Users()
        Log.e(TAG, "UserEditViewModel is cleared")
        super.onCleared()
    }
}


