package space.active.testeroid.screens.edittestlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.RecyclerViewAdapter
import space.active.testeroid.databinding.FragmentEditTestListBinding
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.screens.SharedViewModel
import space.active.testeroid.screens.edittest.EditTestFragment
import space.active.testeroid.screens.main.MainActivityViewModel
import space.active.testeroid.screens.main.MainActivityViewModelFactory
import space.active.testeroid.screens.main.MainActivityFormState
import space.active.testeroid.screens.main.MainActivityUiState


class EditTestListFragment : Fragment() {

    lateinit var binding: FragmentEditTestListBinding
    lateinit var recyclerView: RecyclerView
    lateinit var adapter: RecyclerViewAdapter
    private lateinit var viewModel: EditTestListViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var viewModelMain: MainActivityViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "EditTestListFragment created")
        viewModel = ViewModelProvider(requireActivity(),
            EditTestListViewModelFactory(this.requireContext())
        )
            .get(EditTestListViewModel::class.java)
        viewModelMain = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(this.requireContext())
        )
            .get(MainActivityViewModel::class.java)
        sharedViewModel = ViewModelProvider(this.requireActivity()).get(SharedViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditTestListBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        // Realization of RecyclerView
        recyclerView = binding.rvEditList

        // Connect to adapter interface for realization onClick
        adapter = RecyclerViewAdapter(
            object : RecyclerViewAdapter.ItemClickListener{
                override fun onItemClick(item: RecyclerViewAdapter.AdapterItems) {
                    val position = item.position
                    val itemId = item.itemId
                    clickItemInAdapter(position, itemId)
                }

                override fun onItemLongClick(item: RecyclerViewAdapter.AdapterItems) {
                    clickLongItemInAdapter(item)
                }
            }
        )
        recyclerView.adapter = adapter

        handleExternal()
        observers()
        listeners()
    }

    private fun handleExternal() {

    }

    private fun observers() {
        // Get data from View Model

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allTests.collectLatest { listTest ->
                    val listAdapter = arrayListOf<RecyclerViewAdapter.AdapterItems>()
                    listTest.forEach { test ->
                        listAdapter.add(RecyclerViewAdapter.AdapterItems(itemName = test.testName, itemId = test.testId))
                    }
                    adapter.setList(listAdapter)
                }
            }
        }

        viewModel.selectedTestsList.observe(viewLifecycleOwner) {
                list ->
            Log.e(TAG, "viewModel.selectedTestsList.observe $list")
            if (list.size > 0) {
                sendItemsToAdapter(list)
                showToolBar()
            } else {
                adapter.clearSelected()
                closeToolBar()
            }
        }

        viewModelMain.deleteClick.observe(viewLifecycleOwner){
            onClickDelete()
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.newModalFragment.collectLatest { fragment ->
                    Log.e(TAG, "Click to ADD")
                    startEditTestFragment(null)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.testForEdit.collectLatest { testId ->
                    Log.e(TAG, "Click to Item: $testId")
                    startEditTestFragment(testId)
                }
            }
        }

        lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.formState.collectLatest {
                    when (it) {
                        EditTestListUiState.SelectedItem ->
                            viewModelMain.uiState(
                                MainActivityUiState.ShowNavigation(
                                    MainActivityFormState.Navigation(
                                        delete = true
                                    )
                                )
                            )
                        EditTestListUiState.NotSelectedItem ->
                        viewModelMain.uiState(MainActivityUiState.ShowTabs)
                    }
                }
            }
        }
    }

    private fun startEditTestFragment(testId: Long?) {
        testId?.let {
            sharedViewModel.setTestForEdit(it)
        }
        val fragmentManager = parentFragmentManager
        val newFragment = EditTestFragment()

        // Start new Fragment here
        fragmentManager.commit {
            replace(R.id.frame_main, newFragment)
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    private fun listeners() {
        binding.fbAddTest.setOnClickListener {
            viewModel.onEvent(EditTestListEvents.OnAddClick)
        }
    }

    private fun showToolBar(){
        viewModelMain.uiState(MainActivityUiState.ShowNavigation(
            MainActivityFormState.Navigation(
                add = false, edit = false, delete = true
            )
        ))
    }

    private fun closeToolBar(){
        viewModelMain.uiState(MainActivityUiState.ShowTabs)
    }

    private fun sendItemsToAdapter(list: List<Tests>) {
        val selectedList: List<Long> = list.map { it.testId }
        Log.e(TAG,"setSelected selectedList: $selectedList")
        adapter.setSelected(selectedList)
    }

    private fun onClickDelete(){
        Log.e(TAG, "Click to Delete")
        viewModel.onEvent(EditTestListEvents.OnDeleteClick)
    }

    private fun clickItemInAdapter(position: Int, testId: Long){
        Log.e(TAG,"clickItemInAdapter $position || $testId")
        viewModel.onEvent(EditTestListEvents.OnItemClick(testId))
    }

    private fun clickLongItemInAdapter(item: RecyclerViewAdapter.AdapterItems){
        Log.e(TAG,"clickLongItemInAdapter item.itemId ${item.itemId} item.position ${item.position}")
        viewModel.onEvent(EditTestListEvents.OnItemLongClick(item.itemId))
    }
}