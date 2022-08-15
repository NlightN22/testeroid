package space.active.testeroid.screens.test

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.APP
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentTestBinding
import space.active.testeroid.db.relations.TestWithQuestions

class TestFragment : Fragment() {
    lateinit var binding: FragmentTestBinding
    lateinit var viewModel: TestViewModel

    lateinit var listButtons: List<Button>

    private var listTestWithQuestions = listOf<TestWithQuestions>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "TestFragment created")
        viewModel = ViewModelProvider(APP, TestViewModelFactory(APP.applicationContext)).get(
            TestViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTestBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){

        Log.e(TAG, "TestFragment end initialization")

        listButtons = listOf(
            binding.button1Test,
            binding.button2Test,
            binding.button3Test,
            binding.button4Test,
        )

        listButtons.forEach {
            it.setOnClickListener {nextTest()}
        }

        viewModel.testsSize.observe(viewLifecycleOwner){
            Log.e(TAG, "testsSize: $it")
        }

        viewModel.testsWithQuestions.observe(viewLifecycleOwner){
            list ->
            listTestWithQuestions = list
            Log.e(TAG, "testsWithQuestions set: $listTestWithQuestions")
            try {
                if (list.isNotEmpty()) {
                    val currentIndex = 0 // if list is updated - start from 0
                    val currentTest = list[currentIndex]
                    viewModel.setCurrentTest(currentTest)
                    updateTest(currentTest)}
            } catch (e: Exception) {
                Log.e(TAG, "Error - can't update test. ${e.message}")
            }
        }

        viewModel.currentTest.observe(viewLifecycleOwner){
            updateTest(it)
        }

        viewModel.currentTestIndex.observe(viewLifecycleOwner){
            binding.tvTestCount.text = it.toString()
        }

    }

    fun updateTest(currentTest: TestWithQuestions){
        Log.e(TAG, "updateTest: $currentTest")
        binding.tvIdTest.text = currentTest.tests.testId.toString()
        binding.tvTitleTest.text = currentTest.tests.testName
        binding.button1Test.text = currentTest.questions[0].questionName
        binding.button2Test.text = currentTest.questions[1].questionName
        binding.button3Test.text = currentTest.questions[2].questionName
        binding.button4Test.text = currentTest.questions[3].questionName
    }

    fun nextTest(){
        viewModel.currentTest.value?.let {currentTest ->
            viewModel.testsWithQuestions.value?.let { list ->
                viewModel.setNextTest(list , currentTest)
            }
        }
    }
}

