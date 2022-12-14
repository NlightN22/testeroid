package space.active.testeroid.screens.user

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.RecyclerViewAdapter
import space.active.testeroid.databinding.EditTextInputPasswordBinding
import space.active.testeroid.databinding.FragmentUserBinding
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.repository.DataStoreRepositoryImplementation
import space.active.testeroid.screens.SharedViewModel
import space.active.testeroid.screens.edittestlist.EditTestListEvents
import space.active.testeroid.screens.main.MainActivityViewModel
import space.active.testeroid.screens.main.MainActivityViewModelFactory
import space.active.testeroid.screens.useredit.UserEditEvents
import space.active.testeroid.screens.useredit.UserEditFragment

class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    lateinit var viewModel: UserViewModel
    lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModelMain: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            this,
            UserViewModelFactory(this.requireContext())
        ).get(UserViewModel::class.java)

        viewModelMain = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(this.requireContext())
        ).get(MainActivityViewModel::class.java)

        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        val recyclerView: RecyclerView = binding.recyclerView
        val adapter = RecyclerViewAdapter(
            object : RecyclerViewAdapter.ItemClickListener{
                override fun onItemClick(values: RecyclerViewAdapter.AdapterItems) {
                    val userId = values.itemId
                    onItemClick(userId)
                }

                override fun onItemLongClick(values: RecyclerViewAdapter.AdapterItems) {
                    val userId = values.itemId
                    onItemLongClick(userId)
                }

                override fun onCheckBoxClick(value: RecyclerViewAdapter.AdapterItems) {
                    viewModel.onEvent(UserEvents.OnCheckBoxClick(value.itemId))
                }
            },
            checkBoxVisibility = true
        )
        recyclerView.adapter = adapter
        observers(adapter)
        listeners()
        Log.e(TAG, "UserFragment end initialization")
    }

    private fun observers(adapter: RecyclerViewAdapter) {

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.errorMsg.collectLatest {
                Log.e(TAG,"errorMsg: ${it.asString(requireContext())}")
                toastMessage(it.asString(requireContext()))
            }
        }

        // NB need to start different coroutines for diff variables
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userList.collectLatest { list->
                Log.e(TAG, "userList $list")
                val listAdapter = arrayListOf<RecyclerViewAdapter.AdapterItems>()
                list.forEach { user ->
                    listAdapter.add(RecyclerViewAdapter.AdapterItems(user.userName, user.userId))
                }
                adapter.setList(listAdapter)
                viewModel.selectedUserId.collectLatest {
                    Log.e(TAG, "selectedUser.collectLatest $it")
                    it?.let {
                        adapter.setSelected(listOf(it))
                    }
                }
            }
        }

        viewModel.passwordDialogEvent.observe(viewLifecycleOwner) { userName ->
            inputPasswordDialog(userName)
        }

        viewModel.openEditUserEvent.observe(viewLifecycleOwner) { userForEdit ->
            sharedViewModel.setUserForEdit(userForEdit) // save id to share data
            openFragment(UserEditFragment())
        }
    }

    private fun listeners() {
        binding.fbAddItem.setOnClickListener {
            Log.e(TAG, "Click to ADD")
            viewModel.onEvent(UserEvents.OnAddClick)
//            openFragment(UserEditFragment())
        }
    }

    private fun onItemClick(userId: Long){
        viewModel.onEvent(UserEvents.OnClickItem(userId))
//        openEditFragment(userId)
    }

    private fun onItemLongClick(userId: Long){
        viewModel.onEvent(UserEvents.OnLongClickItem(userId))
    }

    private fun openFragment(newFragment: Fragment){

        val fragmentManager =  this.requireActivity().supportFragmentManager
        fragmentManager.commit {
            replace(R.id.main_container, newFragment)
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    private fun inputPasswordDialog(userName: String) {
        val passwordLayout = EditTextInputPasswordBinding.inflate(layoutInflater)

        val passwordInputDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Input '${userName.uppercase()}' password")
//            .setMessage("Please input user ${userName} password")
            .setView(passwordLayout.root)
            .setPositiveButton(R.string.dialog_OK) {_ , _ ->
                val result = passwordLayout.editTextPassword.text.toString()
                viewModel.onEvent(UserEvents.OkDialogPassword(result))
            }
            .setNegativeButton("Cancel") {_,_ ->
                viewModel.onEvent(UserEvents.CancelDialogPassword)
            }
            .setOnCancelListener {
                viewModel.onEvent(UserEvents.CancelDialogPassword)
                toastMessage("Cancelled") }
            .create()
        passwordInputDialog.show()
    }

    private fun toastMessage(message: String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

class UserViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: DataBaseRepositoryRealization = DataBaseRepositoryRealization(dao)
    private val dataStore: DataStoreRepository = DataStoreRepositoryImplementation(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository, dataStore) as T
    }

}