package space.active.testeroid.screens.edittest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.activity.OnBackPressedCallback
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentEditTestBinding
import space.active.testeroid.screens.edittestlist.EditTestListViewModel
import space.active.testeroid.screens.edittestlist.EditTestListViewModelFactory

class EditTestFragment : Fragment() {
    lateinit var binding: FragmentEditTestBinding
    lateinit var viewModel: EditTestViewModel
    lateinit var viewModelEditList: EditTestListViewModel

    private var listEditVariant = listOf<EditText>()
    private var listOfAllEdits = arrayListOf<EditText>()
    private var listInputLayouts = listOf<TextInputLayout>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentEditTestBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(this, EditTestViewModelFactory(APP.applicationContext))
            .get(EditTestViewModel::class.java)
        viewModelEditList = ViewModelProvider(requireActivity(), EditTestListViewModelFactory(APP.applicationContext))
            .get(EditTestListViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP.binding.pager.visibility = View.GONE // hide pager
        init()
    }

    private fun init() {
        listEditVariant = listOf<EditText>(
            binding.editTextVariant1,
            binding.editTextVariant2,
            binding.editTextVariant3,
            binding.editTextVariant4,
        )

        listInputLayouts = listOf(
            binding.textInputLayout1,
            binding.textInputLayout2,
            binding.textInputLayout3,
            binding.textInputLayout4,
        )
        observers()
        listeners()
    }

    private fun observers() {
        lifecycleScope.launchWhenCreated {
            viewModelEditList.testForEdit.value?.let { testId ->
                viewModel.uiState(EditTestViewModel.EditTestUiState.ShowIncome(testId))
            } ?: run {
                viewModel.uiState(EditTestViewModel.EditTestUiState.ShowNew)
            }
        }

            viewModel.formState.observe(viewLifecycleOwner) { formState ->
                binding.tvId.text = formState.id
                binding.edTitle.setText(formState.title)
                listEditVariant.forEachIndexed { index, editText ->
                    editText.setText(formState.listVariants[index])
                }
                Log.e(TAG, "formState $formState")
                listInputLayouts.forEachIndexed { index, textInputLayout ->
                    textInputLayout.check(formState.listSelected[index])
                }
        }
    }

    fun TextInputLayout.check(state: Boolean) {
        val checked = context?.getColorStateList(R.color.yellow)
        val unchecked = context?.getColorStateList(R.color.gray_50)

        Log.e(TAG, "TextInputLayout.check $state")
        if (state) {this.setEndIconTintList(checked)
        } else {this.setEndIconTintList(unchecked)}
    }

    private fun listeners(){
        binding.edTitle.addTextChangedListener {
            val title = binding.edTitle.text.toString()
//            viewModel.onEvent(EditTestFormEvents.TitleChanged(title))
        }
        listEditVariant.forEachIndexed { index, editText ->
            editText.addTextChangedListener {
//                viewModel.onEvent(EditTestFormEvents.VariantChanged(editText.text.toString(), index))
            }
        }
        listInputLayouts.forEachIndexed { index, textInputLayout ->
            textInputLayout.setEndIconOnClickListener {
                Log.e(TAG, "setEndIconOnClickListener: $index")
                viewModel.onEvent(EditTestFormEvents.CheckChanged(index))
            }
        }
        binding.btnAdd.setOnClickListener {

        }
        binding.btnClose.setOnClickListener {
            viewModel.onEvent(EditTestFormEvents.Cancel)
        }
    }

    fun onClickCheck(index: Int) {

    }

    override fun onDestroy() {
        viewModelEditList.clearTestForEdit()
        APP.binding.pager.visibility = View.VISIBLE // show pager
        Log.e(TAG, "EditTest onDestroy: ${viewModelEditList.selectedTestsList}")
        super.onDestroy()
    }
}

//
//    private fun init(){
//        stateViewControllers() // Control and save interface statement
//        setValuesFromExternal() // set incoming data to interface !After statements controllers
//        listeners()
//    }
//
//    private fun stateViewControllers(){
//        listEditVariant = listOf<EditText>(
//            binding.editTextVariant1,
//            binding.editTextVariant2,
//            binding.editTextVariant3,
//            binding.editTextVariant4,
//        )
//
//        listOfAllEdits = ArrayList(listEditVariant)
//        listOfAllEdits.add(binding.edTitle)
//    }
//
//    private fun setValuesFromExternal(){
//        val currentTest = viewModelEditList.testForEdit.value
//        Log.e(TAG, "EditTestFragment created")
//        Log.e(TAG, "Come to EditTest $currentTest")
//        if (currentTest != null) {
//            // If we edit incoming data
//            viewModel.setCurrentTest(currentTest)
////            drawCurrentTest(currentTest)
//        } else {
//            // if add new data
//            viewModel.setViewStateForAdd(listEditVariant.size)
//        }
//        drawCurrentTest()
//    }
//
//    private fun listeners(){
//
//        binding.btnAdd.setOnClickListener {
//            onClickAdd()
//        }
//
//        binding.btnClose.setOnClickListener {
//            onClickClose()
//        }
//
//        listInputLayouts = listOf(
//            binding.textInputLayout1,
//            binding.textInputLayout2,
//            binding.textInputLayout3,
//            binding.textInputLayout4,
//        )
//        listInputLayouts.forEachIndexed {
//                index, inputLayout ->
//            inputLayout.setEndIconOnClickListener {
//                onClickVariant(index)
//            }
//        }
//
//        listEditVariant.forEachIndexed() { index, text ->
//            text.addTextChangedListener {
//                viewModel.setTextToVariants(index, it.toString())
//            }
//        }
//    }
//
//    private fun drawCurrentTest(){
//        viewModel.id.observe(viewLifecycleOwner){ binding.tvId.text = it.toString()}
//        viewModel.title.observe(viewLifecycleOwner){ binding.edTitle.setText(it)}
//
//        viewModel.variantList.observe(viewLifecycleOwner) { variantList->
//            Log.e(TAG, "drawCurrentTest variantList: $variantList")
//            if (variantList.isNotEmpty()) {
//                variantList.forEachIndexed { index, variant ->
//                    viewModelEditList.testForEdit.value?.let {
//                        listEditVariant[variant.position].setText(variant.question.questionName)
//                    }
//                    setTextInputLayoutCheck(listInputLayouts[variant.position], variant.question.correctAnswer)
//                }
//            } else {
//                Log.e(TAG, "Error fun drawCurrentTest variantList is $variantList")
//            }
//        }
//    }
//
//    private fun setTextInputLayoutCheck(inputLayout: TextInputLayout, state: Boolean){
//        val checked = context?.getColorStateList(R.color.yellow)
//        val unchecked = context?.getColorStateList(R.color.gray_50)
//
//        if (state) {inputLayout.setEndIconTintList(checked)
//        } else {inputLayout.setEndIconTintList(unchecked)}
//    }
//
//    private fun onClickVariant(position: Int){
//        Log.e(TAG, "onClickVariant: $position")
//        viewModel.setCorrectAnswer(position)
//    }
//
//    private fun onClickAdd(){
//        val testId: String = binding.tvId.text.toString()
//        val testName: String = binding.edTitle.text.toString()
//        val variantList: List<String> = listEditVariant.map { it.text.toString() }
//
//        viewModel.preparingDataForSending(testId, testName, variantList)
//
//        onClickClose()
//    }
//
//    private fun clearFragment() {
//        listOfAllEdits.forEach {
//            it.text.clear()
//        }
//        binding.tvId.text = ""
//        viewModel.clearCorrectVariants()
//    }
//
//    private fun onClickClose() {
//        parentFragmentManager.popBackStack()
//    }
//
//    override fun onDestroy() {
//        APP.binding.pager.visibility = View.VISIBLE // show pager
//        viewModelEditList.clearTestForEdit()
//        Log.e(TAG, "EditTest onDestroy: ${viewModelEditList.selectedTestsList}")
//        super.onDestroy()
//    }
//
//}