package space.active.testeroid.screens.useredit

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.repository.Repository

class UserEditViewModel(private val repository: Repository): ViewModel() {

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

    fun isLastAdministrator(userList: List<Users>): Boolean {
        _currentUser.value?.let { user ->
            val listAdmin = userList.filter { it.userAdministrator }
            if (listAdmin.size <= 1 && user.userAdministrator) {
                return true
            }
        }
        return false
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

    override fun onCleared() {
        Log.e(TAG, "UserEditViewModel is cleared")
        super.onCleared()
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


