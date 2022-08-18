package space.active.testeroid.screens.edittestlist

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.commit
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.recyclerview.widget.RecyclerView
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.adapter.RecyclerViewAdapter
import space.active.testeroid.databinding.FragmentEditTestListBinding
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.screens.SharedViewModel
import space.active.testeroid.screens.main.MainActivityViewModel
import space.active.testeroid.screens.main.MainActivityViewModelFactory
import space.active.testeroid.screens.edittest.EditTestFragment


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
            EditTestListViewModelFactory(APP.applicationContext)
        )
            .get(EditTestListViewModel::class.java)
        viewModelMain = ViewModelProvider(
            requireActivity(),
            MainActivityViewModelFactory(APP.applicationContext)
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
                override fun onItemClick(item: RecyclerViewAdapter.AdapterValues) {
                    val position = item.position
                    val itemId = item.itemId
                    clickItemInAdapter(position, itemId)
                }

                override fun onItemLongClick(item: RecyclerViewAdapter.AdapterValues) {
                    clickLongItemInAdapter(item)
                }
            }
        )
        recyclerView.adapter = adapter

        // Get data from View Model
        viewModel.allTests.observe(APP) {listTest ->
            val listAdapter = arrayListOf<RecyclerViewAdapter.AdapterValues>()
            listTest.forEach { test ->
                listAdapter.add(RecyclerViewAdapter.AdapterValues(itemName = test.testName, itemId = test.testId))
            }
            adapter.setList(listAdapter)
        }

        viewModel.selectedTestsList.observe(APP) {
            list ->
            Log.e(TAG, "selectedTestsList $list")
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

        binding.fbAddTest.setOnClickListener {
            onClickAdd()
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

    // TODO overcoding delete this fun
    private fun sendItemsToAdapter(list: List<Tests>) {
        val selectedList: List<Long> = list.map { it.testId }
        Log.e(TAG,"setSelected selectedList: $selectedList")
        selectedList.forEach {
            adapter.setSelected(it)
        }
    }

    private fun onClickDelete(){
        Log.e(TAG, "Click to Delete")
        viewModel.deleteTestsWithQuestions()
    }

    private fun onClickAdd(){
        Log.e(TAG, "Click to ADD")
        val fragmentManager = APP.supportFragmentManager
        val newFragment = EditTestFragment()
        fragmentManager.commit {
            replace(R.id.main_container, newFragment)
            addToBackStack(null)
            setReorderingAllowed(true)
        }
    }

    private fun clickItemInAdapter(position: Int, testId: Long){
        Log.e(TAG,"clickItemInAdapter $position || $testId")
        viewModelMain.bottomToolBarVisibility.value?.let {
            if (!it) {
                val fragmentManager = parentFragmentManager
                val newFragment = EditTestFragment()
                // Send data to EditTest
                sharedViewModel.setTestForEdit(testId)
                // Start new Fragment here
                fragmentManager.commit {
                    replace(R.id.frame_main, newFragment)
                    addToBackStack(null)
                    setReorderingAllowed(true)
                }
            }
        } ?: run {
            Log.e(TAG,
                "Error private fun clickItemInAdapter bottomToolBarVisibility: ${viewModelMain.bottomToolBarVisibility.value}")
        }
    }

    private fun clickLongItemInAdapter(item: RecyclerViewAdapter.AdapterValues){
        Log.e(TAG,"clickLongItemInAdapter item.itemId ${item.itemId} item.position ${item.position}")
        viewModel.selectListItem(item.itemId)
    }
}