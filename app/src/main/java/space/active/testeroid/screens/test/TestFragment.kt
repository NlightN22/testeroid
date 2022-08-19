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
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentTestBinding

class TestFragment : Fragment() {
    lateinit var binding: FragmentTestBinding
    lateinit var viewModel: TestViewModel

    lateinit var listButtons: List<Button>

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
        listButtons = listOf(
            binding.button1Test,
            binding.button2Test,
            binding.button3Test,
            binding.button4Test,
        )
        externalData()
        observers()
        listeners()
        Log.e(TAG, "TestFragment end initialization")
    }

    private fun externalData(){
        viewModel.testsWithQuestions.observe(viewLifecycleOwner){ list->
            viewModel.uiState(TestUiState.ShowFirst(list))
        }
    }
    private fun observers() {
        viewModel.formState.observe(viewLifecycleOwner){ form->
            binding.tvIdTest.text = form.id
            binding.tvTestCount.text = form.count
            binding.tvTestSize.text = form.size
            binding.tvTitleTest.text = form.title
            binding.button1Test.text = form.variant1
            binding.button2Test.text = form.variant2
            binding.button3Test.text = form.variant3
            binding.button4Test.text = form.variant4
            listButtons.forEachIndexed { index, button ->
                button.correct(form.correctList[index])
            }
            // TODO add score
        }

    }
    private fun Button.correct(value: AnswerColor) {
        val correct = context?.getColorStateList(R.color.green)
        val notCorrect = context?.getColorStateList(R.color.dark_red)
        val neutral = context?.getColorStateList(R.color.dark_blue)
        when (value) {
            AnswerColor.Ok -> {this.backgroundTintList = correct}
            AnswerColor.NotOk -> {this.backgroundTintList = notCorrect}
            else -> {this.backgroundTintList = neutral}
        }
    }

    private fun listeners() {
        binding.button1Test.setOnClickListener { viewModel.onEvent(TestFormEvents.Variant1) }
        binding.button2Test.setOnClickListener { viewModel.onEvent(TestFormEvents.Variant2) }
        binding.button3Test.setOnClickListener { viewModel.onEvent(TestFormEvents.Variant3) }
        binding.button4Test.setOnClickListener { viewModel.onEvent(TestFormEvents.Variant4) }
    }
}

