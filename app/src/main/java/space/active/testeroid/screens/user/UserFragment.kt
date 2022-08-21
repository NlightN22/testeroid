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
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.RecyclerViewAdapter
import space.active.testeroid.databinding.EditTextInputPasswordBinding
import space.active.testeroid.databinding.FragmentUserBinding
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.screens.main.MainActivityViewModel
import space.active.testeroid.screens.main.MainActivityViewModelFactory
import space.active.testeroid.screens.useredit.UserEditFragment

class UserFragment : Fragment() {
    lateinit var binding: FragmentUserBinding
    lateinit var viewModel: UserViewModel
    private lateinit var viewModelMain: MainActivityViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentUserBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            requireActivity(),
            UserViewModelFactory(this.requireContext())
        ).get(UserViewModel::class.java)

        viewModelMain = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(this.requireContext())
        ).get(MainActivityViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init() {
        Log.e(TAG, "UserFragment end initialization")
        fillRecyclerView()

        binding.fbAddItem.setOnClickListener {
            onAddClick()
        }

        viewModelMain.deleteClick.observe(viewLifecycleOwner){
            onDeleteClick()
        }
    }

    private fun fillRecyclerView(){
        val recyclerView: RecyclerView = binding.recyclerView
        val adapter = RecyclerViewAdapter(
            object : RecyclerViewAdapter.ItemClickListener{
                override fun onItemClick(values: RecyclerViewAdapter.AdapterValues) {
                    val userId = values.itemId
                    onItemClick(userId)
                }

                override fun onItemLongClick(values: RecyclerViewAdapter.AdapterValues) {
                    val userId = values.itemId
                    onItemLongClick(userId)
                }
            }
        )
        recyclerView.adapter = adapter

        viewModel.userList.observe(viewLifecycleOwner) { list->
            Log.e(TAG, "userList $list")
            val listAdapter = arrayListOf<RecyclerViewAdapter.AdapterValues>()
            list.forEach { user ->
                listAdapter.add(RecyclerViewAdapter.AdapterValues(user.userName, user.userId))
            }
            adapter.setList(listAdapter)
        }

        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            viewModel.selectedUser.collectLatest {
                it?.let {
                    adapter.setSelected(listOf(it))
                }
            }
        }

    }

    private fun showToolBar(){
        viewModelMain.setViewState(MainActivityViewModel.ViewStateMain.BottomToolBar(true))
        viewModelMain.setViewState(
            MainActivityViewModel.ViewStateMain.BottomToolBarButtons(
            add = false,
            edit = true,
            delete = true
        ))
    }

    private fun closeToolBar(){
        viewModelMain.setViewState(MainActivityViewModel.ViewStateMain.BottomToolBar(false))
    }

    private fun onItemClick(userId: Long){
        Log.e(TAG, "Click to Item")
        openEditFragment(userId)
    }

    private fun onItemLongClick(userId: Long){
        viewModel.selectUserListItem(userId)
    }

    private fun onAddClick(){
        Log.e(TAG, "Click to ADD")
        // open UserEdit cleared
        openEditFragment()
    }

    private fun openEditFragment(userId: Long = 0){
        // ask password if it is
        if (userId > 0) {
//            Log.e(TAG, "setUserForEdit userId: $userId")
            viewModel.setUserForEdit(userId)
            viewModel.passwordDialogEvent.observe(viewLifecycleOwner) { userName ->
                inputPasswordDialog(userName)
            }
            viewModel.passwordCheckResult.observe(viewLifecycleOwner) { result ->
                if (result == UserViewModel.CheckState.Ok) {
                    openFragment(UserEditFragment())
                }
                else if (result == UserViewModel.CheckState.NotOk) {
                    toastMessage(getString(R.string.user_toast_wrong_password))
                }
            }
        } else {
            openFragment(UserEditFragment())
        }
    }

    private fun openFragment(newFragment: Fragment){

        val fragmentManager = APP.supportFragmentManager
        fragmentManager.commit {
            replace(R.id.main_container, newFragment)
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    private fun onDeleteClick(){
        // ask password if it is
        // delete
    }

    private fun inputPasswordDialog(userName: String) {
        val passwordLayout = EditTextInputPasswordBinding.inflate(layoutInflater)

        val passwordInputDialog = AlertDialog.Builder(this.requireContext())
            .setTitle("Input '${userName.uppercase()}' password")
//            .setMessage("Please input user ${userName} password")
            .setView(passwordLayout.root)
            .setPositiveButton(R.string.dialog_OK) {_ , _ ->
                val result = passwordLayout.editTextPassword.text.toString()
                viewModel.checkUserPassword(result)
            }
            .setNegativeButton("Cancel") {_,_ ->}
            .setOnCancelListener { toastMessage("Cancelled") }
//            .setOnDismissListener {
//                DialogInterface.OnClickListener { dialogInterface, i ->
//                    when (i) {
//                        DialogInterface.BUTTON_POSITIVE -> toastMessage("Ok")
//                        DialogInterface.BUTTON_NEGATIVE -> toastMessage("Cancel")
//                    }
//                }
//            }
            .create()

        passwordInputDialog.show()
    }

    private fun toastMessage(message: String){
        Toast.makeText(this.requireContext(), message, Toast.LENGTH_SHORT).show()
    }
}

class UserViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: RepositoryRealization = RepositoryRealization(dao)
    private val dataStore: DataStoreRepository = DataStoreRepository(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository, dataStore) as T
    }

}