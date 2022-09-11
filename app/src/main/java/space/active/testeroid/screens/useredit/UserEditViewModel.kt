package space.active.testeroid.screens.useredit

import android.util.Log
import androidx.datastore.dataStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
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
                    viewModelScope.launch {
                        repository.allUsers().collectLatest { list->
                            if (list.size <= 1) {
                                Log.e(TAG, "UserEditUiState.LastAdmin")
                                form.adminEnabled = false
                                form.administrator = true
                                _formState.notifyObserver()
                            }
                        }
                    }

                }
            }
            is UserEditUiState.ErrorMessage -> {}
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
                            }
                        }?: run {
                            _editedUser = Users()
                            uiState(UserEditUiState.NewUser)
                        }
                    } else {
                        uiState(UserEditUiState.RestoreForm)
                    }
                    uiState(UserEditUiState.LastAdmin)
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
                viewModelScope.launch {
                    repository.addUser(_editedUser)
                    _terminateSignal.emit(true)
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

    fun isLastAdministrator(userList: List<Users>): Boolean {
        val listAdmin = userList.filter { it.userAdministrator }
        if (listAdmin.size <= 1) {
            return true
        }
    return false
    }

    override fun onCleared() {
        _editedUser = Users()
        Log.e(TAG, "UserEditViewModel is cleared")
        super.onCleared()
    }

// TODO add to Destroy                    _formState.value?.let { it.active = false }



    private val _adminCheckBox = MutableLiveData(ViewState.AdminCheckBox())
    val adminCheckBox: LiveData<ViewState.AdminCheckBox> = _adminCheckBox

    private val _cancelEnabled = MutableLiveData(ViewState.CancelButton())
    val cancelEnabled: LiveData<ViewState.CancelButton> = _cancelEnabled

    private val _backButton = MutableLiveData(ViewState.BackButton())
    val backButton: LiveData<ViewState.BackButton> = _backButton

    private val _validateForm = MutableLiveData(ViewState.ValidateForm())
    val validateForm: LiveData<ViewState.ValidateForm> = _validateForm

    private val _currentUser = MutableLiveData<Users>()
    val currentUser: LiveData<Users> = _currentUser

    private val _toastMessage = MutableLiveData<Int>()
    val toastMessage: LiveData<Int> = _toastMessage

    private val _deleteUserEvent = MutableLiveData<Boolean>()
    val deleteUserEvent: LiveData<Boolean> = _deleteUserEvent


    fun setCurrentUser(user: Users){
        _currentUser.value = user
    }

    fun blockLastAdministrator(userList: List<Users>){
        // TODO add to main activity start this fragment if userList is Null
        if (isLastAdministrator(userList)) {
            // Checkbox Administrator is checked and not editable
            elementsViewState(
                ViewState.AdminCheckBox(
                    visible = true,
                    checkable = false,
                    checked = true
                )
            )
            // button_back change behavior for close app
            elementsViewState(ViewState.BackButton(enabled = false))
        }
    }

    fun elementsViewState(viewState: ViewState){
        when(viewState) {
            is ViewState.AdminCheckBox -> {
                _adminCheckBox.value?.apply {
                    visible = viewState.visible
                    checkable = viewState.checkable
                    checked = viewState.checked
                }
            }
            is ViewState.BackButton -> {
                _backButton.value?.enabled = viewState.enabled
            }
            is ViewState.CancelButton -> {
                _cancelEnabled.value?.enabled = viewState.enabled
            }
            is ViewState.ValidateForm -> {
                TODO()
            }
        }
    }

    fun validateAndSaveValues(userName: String, password: String, id: String){
        // Control username and password is not empty
        if (userName.isNotEmpty()) {
            val user = Users(userName = userName, userPassword = password)
            if (id.isNotEmpty()) { user.userId = id.toLong() }
            _adminCheckBox.value?.let {
                //control for admin. need password
                if (it.checked) {
                    user.userAdministrator = true
                    if (password.isNotEmpty()) {
                        saveCredentials(user)
                        _validateForm.value!!.result = true
                    } else {
                        _validateForm.value!!.result = false
                        _toastMessage.value = R.string.edit_user_empty_password
                    }
                } else {
                    saveCredentials(user)
                    _validateForm.value!!.result = true
                }
            }?: run {
                val msg = "Error fun onOkClick _adminCheckBox.value is ${_adminCheckBox.value}"
                Log.e(TAG, msg)
                _validateForm.value!!.result = false
//                _validateForm.value!!.message = msg
            }
        }else{
            _validateForm.value!!.result = false
            _toastMessage.value = R.string.edit_user_empty_username
        }
    }

    //Send to Database
    fun saveCredentials(user: Users){
        viewModelScope.launch(Dispatchers.IO) {
            repository.addUser(user)
        }
    }

   fun deleteCredentials(userList: List<Users>){
       viewModelScope.launch(Dispatchers.IO) {
           _currentUser.value?.let { user ->
               if (isLastAdministrator(userList)) {
                   _toastMessage.postValue(R.string.edit_user_last_admin)
               } else  {
                   repository.deleteUser(user)
                   _deleteUserEvent.postValue(true)
               }
           }?: run {
               Log.e(TAG, "Error, fun deleteCredentials _currentUser.value: ${_currentUser.value}")
           }
       }
   }

    sealed class ViewState{
        data class AdminCheckBox(
            var visible: Boolean = true,
            var checkable: Boolean = true,
            var checked: Boolean = false
        ): ViewState()

        data class CancelButton(
            var enabled: Boolean = true
        ): ViewState()

        data class BackButton(
            var enabled: Boolean = true
        ): ViewState()

        data class ValidateForm(
            var result: Boolean = false,
        ): ViewState()
    }

}


