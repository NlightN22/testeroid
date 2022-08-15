package space.active.testeroid.screens.edittest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textfield.TextInputLayout
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
        viewModel = ViewModelProvider(requireActivity(), EditTestViewModelFactory(APP.applicationContext))
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
    private fun init(){

        val currentTest = viewModelEditList.testForEdit.value
        Log.e(TAG, "EditTestFragment created")
        Log.e(TAG, "Come to EditTest $currentTest")
        if (currentTest != null) {
            viewModel.setCurrentTest(currentTest)
//            drawCurrentTest(currentTest)
        }
        drawCurrentTest()


        binding.btnAdd.setOnClickListener {
            onClickAdd()
        }

        listEditVariant = listOf<EditText>(
            binding.editTextVariant1,
            binding.editTextVariant2,
            binding.editTextVariant3,
            binding.editTextVariant4,
        )
//
//        listOfAllEdits = ArrayList(listEditVariant)
//        listOfAllEdits.add(binding.edTitle)
//
         listInputLayouts = listOf(
            binding.textInputLayout1,
            binding.textInputLayout2,
            binding.textInputLayout3,
            binding.textInputLayout4,
        )

        listInputLayouts.forEachIndexed {
                index, inputLayout ->
            inputLayout.setEndIconOnClickListener {
                onClickVariant(index)
            }
        }
    }

    private fun drawCurrentTest(){
        viewModel.id.observe(viewLifecycleOwner){ binding.tvId.text = it.toString()}
        viewModel.title.observe(viewLifecycleOwner){ binding.edTitle.setText(it)}

        viewModel.variantList.observe(viewLifecycleOwner) { variantList->
            if (variantList.isNotEmpty()) {
                Log.e(TAG, "drawCurrentTest variantList: $variantList")

                // set text for EditText
                listEditVariant.mapIndexed { index, view ->
                    view.setText(variantList[index].text)
                }
                // set Icon Tint for TextInputLayout
                listInputLayouts.mapIndexed{ index, textInputLayout ->
                    setTextInputLayoutCheck(textInputLayout, variantList[index].checked)
                }
            } else {
                Log.e(TAG, "Error fun drawCurrentTest variantList is $variantList")
            }
        }
    }

    private fun setTextInputLayoutCheck(inputLayout: TextInputLayout, state: Boolean){
        val checked = context?.getColorStateList(R.color.yellow)
        val unchecked = context?.getColorStateList(R.color.gray_50)

        if (state) {inputLayout.setEndIconTintList(checked)
        } else {inputLayout.setEndIconTintList(unchecked)}
    }

    private fun onClickVariant(position: Int){
        Log.e(TAG, "onClickVariant: $position")
        viewModel.onClickVariantEdit(position)
    }

    private fun onClickAdd(){


//        // Check testId if it not null than replace item
//        Log.e(TAG, "Text in tvId: ${binding.tvId.text.isEmpty()}")
//        val test = if (binding.tvId.text.isEmpty()) {
//            Tests(testName = binding.edTitle.text.toString())
//        }else {
//            Tests(testName = binding.edTitle.text.toString(),
//                testId = binding.tvId.text.toString().toLong())
//        }
//        // Make variant list and set text from editTextVariant
//        val variantList =  listOf<Questions>(
//            Questions(questionName = binding.editTextVariant1.text.toString()),
//            Questions(questionName = binding.editTextVariant2.text.toString()),
//            Questions(questionName = binding.editTextVariant3.text.toString()),
//            Questions(questionName = binding.editTextVariant4.text.toString()),
//        )
//
//        // Try to set true for corrected answers
//        try {
//            variantList.forEachIndexed() { index, question ->
//                if (viewModel.selectedCorrectVariants.value!!.contains(index)) {
//                    question.correctAnswer = true
//                }
//            }
//        }catch (e: Exception) {
//            Log.e(TAG, "variantList.forEachIndexed() not set a value $e")
//        }
//
//        Log.e(TAG, "title: $test, listVar: $variantList")
//        // Send data to Database
//        viewModel.addNewTestWithQuestions(test = test, questions = variantList)
//

        clearFragment()
    }

    private fun clearFragment() {
        listOfAllEdits.forEach {
            it.text.clear()
        }
        viewModel.clearCorrectVariants()
    }

    override fun onDestroy() {
        APP.binding.pager.visibility = View.VISIBLE // show pager
        viewModelEditList.clearTestForEdit()
        Log.e(TAG, "EditTest onDestroy: ${viewModelEditList.selectedTestsList}")
        super.onDestroy()
    }

}