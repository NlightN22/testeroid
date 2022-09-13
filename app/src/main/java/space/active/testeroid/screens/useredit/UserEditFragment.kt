package space.active.testeroid.screens.useredit

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentUserEditBinding
import space.active.testeroid.screens.SharedViewModel
import space.active.testeroid.screens.main.MainActivityUiState

class UserEditFragment : Fragment() {

    lateinit var binding: FragmentUserEditBinding
    lateinit var viewModel: UserEditViewModel
    lateinit var sharedViewModel: SharedViewModel

//    lateinit var viewModelUser: UserViewModel

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
        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)


//        viewModelUser = ViewModelProvider(requireActivity(),// Need to set MainActivity if want to share ViewModels data
//            UserViewModelFactory(this.requireContext())
//        )
//            .get(UserViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP.viewModel.uiState(MainActivityUiState.ShowModalFragment)
        init()
    }

    private fun init(){
        // TODO clear sharedviewmodel userforedit
        Log.e(TAG, "${this} created")

        handleExternal()
        observers()
        listeners()


//        stateViewControllers() // Control and save interface statement
//        setValuesFromExternal() // set incoming data to interface !After statements controllers
//        buttonListeners()
    }

    private fun handleExternal() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            val editedUser = sharedViewModel.editedUser.value
            viewModel.onEvent(UserEditEvents.OpenFragment(editedUser))
            sharedViewModel.clearUserForEdit()
        }
    }

    private fun observers() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMsg.collectLatest {
                Log.e(TAG,"errorMsg: ${it.asString(requireContext())}")
                Toast.makeText(requireContext(), it.asString(requireContext()), Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.formState.observe (viewLifecycleOwner) { form->
            binding.textViewId.text = form.id
            binding.editTextUsername.setText(form.username.text)
            binding.editTextUsername.isEnabled = form.username.enabled
            binding.editTextPassword.setText(form.password.text)
            binding.editTextPassword.isEnabled = form.password.enabled
            binding.checkBoxAdmin.isChecked = form.administrator.checked
            binding.checkBoxAdmin.isEnabled = form.administrator.enabled
            binding.buttonDelete.isEnabled = form.deleteEnabled
            binding.buttonSelect.isEnabled = form.selectedEnabled
        }

        lifecycleScope.launchWhenResumed {
            viewModel.terminateSignal.collectLatest { terminate->
                if (terminate) {
                    parentFragmentManager.popBackStack()
                }
            }
        }
    }

    private fun listeners() {
        binding.buttonOK.setOnClickListener {
            viewModel.onEvent(UserEditEvents.OnOkClick)
        }
        binding.buttonCancel.setOnClickListener {
            viewModel.onEvent(UserEditEvents.OnCancelClick)
        }
        binding.buttonDelete.setOnClickListener {
            viewModel.onEvent(UserEditEvents.OnDeleteClick)
        }
        binding.buttonSelect.setOnClickListener {
            viewModel.onEvent(UserEditEvents.OnSelectClick)
        }
        binding.checkBoxAdmin.setOnClickListener {
            viewModel.onEvent(UserEditEvents.OnAdminCheckboxClick)
        }

        lifecycleScope.launchWhenResumed {
            binding.editTextUsername.addTextChangedListener {
                it?.let {
                    viewModel.onEvent(UserEditEvents.OnEditUsername(it.toString()))
                }
            }
            binding.editTextPassword.addTextChangedListener {
                it?.let {
                    viewModel.onEvent(UserEditEvents.OnEditPassword(it.toString()))
                }
            }
        }
    }

    override fun onDestroy() {
        APP.viewModel.uiState(MainActivityUiState.CloseModalFragment)
        Log.e(TAG, "EditUser onDestroy")
        super.onDestroy()
    }

    private fun alertDialog(){
        val singleOkDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Need user")
            .setMessage("You need to add minimum one user")
            .setPositiveButton(R.string.dialog_OK) {_ , _ ->}.create()
        singleOkDialog.show()
    }

    private fun backPressOverride(){
        requireActivity()
            .onBackPressedDispatcher
            .addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    Log.d(TAG, "Fragment back pressed invoked")
                    // Do custom work here

//                // if you want onBackPressed() to be called as normal afterwards
//                if (isEnabled) {
//                    isEnabled = false
//                    requireActivity().onBackPressed()
//                }
                }
            }
            )
    }
}

