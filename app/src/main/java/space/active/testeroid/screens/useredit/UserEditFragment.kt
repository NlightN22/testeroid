package space.active.testeroid.screens.useredit

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentUserEditBinding
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.screens.user.UserViewModel
import space.active.testeroid.screens.user.UserViewModelFactory

class UserEditFragment : Fragment() {

    lateinit var binding: FragmentUserEditBinding
    lateinit var viewModel: UserEditViewModel
    lateinit var viewModelUser: UserViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserEditBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this,
            UserEditViewModelFactory(this.requireContext())
        )
            .get(UserEditViewModel::class.java)
        viewModelUser = ViewModelProvider(requireActivity(),// Need to set MainActivity if want to share ViewModels data
            UserViewModelFactory(this.requireContext())
        )
            .get(UserViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP.binding.pager.visibility = View.GONE // hide pager

        init()
    }

    private fun init(){
        Log.e(TAG, "${this} created")
        stateViewControllers() // Control and save interface statement
        setValuesFromExternal() // set incoming data to interface !After statements controllers
        buttonListeners()
    }

    private fun stateViewControllers(){
        viewModel.adminCheckBox.observe(viewLifecycleOwner){
            binding.checkBoxAdmin.apply {
                isChecked = it.checked
                isEnabled = it.checkable
                visibility = if (it.visible) {View.VISIBLE} else {View.INVISIBLE}
            }
        }

        viewModel.cancelEnabled.observe(viewLifecycleOwner){
            binding.buttonCancel.isEnabled = it.enabled
        }

        viewModel.toastMessage.observe(viewLifecycleOwner){
            val msg = getString(it)
            toastMessage(msg)
        }
    }

    private fun buttonListeners(){
        binding.buttonOK.setOnClickListener {
            onClickOk()
        }

        binding.buttonCancel.setOnClickListener {
            onClickCancel()
        }

        binding.buttonDelete.setOnClickListener {
            onClickDelete()
        }

        binding.checkBoxAdmin.setOnClickListener {
            onClickCheckAdmin()
        }
    }

    private fun setValuesFromExternal() {
        viewModelUser.userForEdit.value?.let {
            viewModel.setCurrentUser(it)
            viewModelUser.userList.value?.let { viewModel.blockLastAdministrator(it) }
        }?: run {
            Log.e(TAG, "userForEdit.value: ${viewModelUser.userForEdit.value}")
        }

        viewModel.currentUser.observe(viewLifecycleOwner) {
            Log.e(TAG, "viewModelUser.userForEdit userName ${it.userName}")
            updateView(it)
        }
    }

    private fun updateView(users: Users){
        binding.textViewId.text = users.userId.toString()
        binding.editTextUsername.setText(users.userName)
        binding.editTextPassword.setText(users.userPassword)
        binding.checkBoxAdmin.isChecked = users.userAdministrator
    }

    private fun onClickCheckAdmin(){
        val state = binding.checkBoxAdmin.isChecked
        viewModel.elementsViewState(UserEditViewModel.ViewState.AdminCheckBox(checked = state))
    }

    private fun onClickOk(){
        val userName = binding.editTextUsername.text.toString()
        val password = binding.editTextPassword.text.toString()
        val id: String = binding.textViewId.text.toString()
        viewModel.validateAndSaveValues(userName, password, id)
        viewModel.validateForm.observe(viewLifecycleOwner){ validate ->
            if (validate.result) { parentFragmentManager.popBackStack() }
        }
    }

    private fun onClickCancel(){
        parentFragmentManager.popBackStack()
    }

    private fun onClickDelete(){
        //send data to repository
        viewModelUser.userList.value?.let {
            viewModel.deleteCredentials(it)
        }
        viewModel.deleteUserEvent.observe(viewLifecycleOwner) {
            parentFragmentManager.popBackStack()
        }
        //close fragment
    }

    override fun onDestroy() {
        APP.binding.pager.visibility = View.VISIBLE // show pager
        viewModelUser.clearUserForEdit()
        Log.e(TAG, "EditUser onDestroy: ${viewModelUser.userForEdit}")
        super.onDestroy()
    }

    private fun alertDialog(){
        val singleOkDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Need user")
            .setMessage("You need to add minimum one user")
            .setPositiveButton(R.string.dialog_OK) {_ , _ ->}.create()
        singleOkDialog.show()
    }

    private fun toastMessage(message: String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

