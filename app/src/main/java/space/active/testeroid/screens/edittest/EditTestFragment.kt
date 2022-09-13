package space.active.testeroid.screens.edittest

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.flow.collectLatest
import space.active.testeroid.APP
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentEditTestBinding
import space.active.testeroid.screens.SharedViewModel
import space.active.testeroid.screens.main.MainActivityUiState

class EditTestFragment : Fragment() {
    lateinit var binding: FragmentEditTestBinding
    lateinit var viewModel: EditTestViewModel
//    lateinit var viewModelEditList: EditTestListViewModel
    lateinit var sharedViewModel: SharedViewModel

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
        sharedViewModel = ViewModelProvider(this.requireActivity()).get(SharedViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        APP.viewModel.uiState(MainActivityUiState.ShowModalFragment)
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
        externalData()
        observers()
        listeners()
    }

    private fun externalData() {
        sharedViewModel.testForEdit.value?.let { testId ->
            lifecycleScope.launchWhenResumed {
            Log.e(TAG, "private fun externalData() sharedViewModel.testForEdit")
            viewModel.uiState(EditTestUiState.ShowIncome(testId))
            sharedViewModel.clearTestForEdit()
            }
        }
    }

    private fun observers() {
        viewModel.formState.observe(viewLifecycleOwner) { formState ->
            binding.tvId.text = formState.id
            binding.edTitle.setText(formState.title)
            listEditVariant[0].setText(formState.variant1)
            listEditVariant[1].setText(formState.variant2)
            listEditVariant[2].setText(formState.variant3)
            listEditVariant[3].setText(formState.variant4)
            Log.e(TAG, "formState $formState")
            listInputLayouts.forEachIndexed { index, textInputLayout ->
                textInputLayout.check(formState.listSelected[index])
            }
        }

//        viewModel.uiState.observe(viewLifecycleOwner) { uiState ->
//            Log.e(TAG, "viewModel.uiState.observe: $uiState")
//        }

        lifecycleScope.launchWhenStarted {
            viewModel.uiEvent.collectLatest { validationResult->
                validationResult.errorMessage?.let { msg ->
                    Snackbar.make(
                        binding.root,
                        getString(msg),
                        Snackbar.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    fun TextInputLayout.check(state: Boolean) {
        val checked = context?.getColorStateList(R.color.yellow)
        val unchecked = context?.getColorStateList(R.color.gray_50)

        if (state) {this.setEndIconTintList(checked)
        } else {this.setEndIconTintList(unchecked)}
    }

    private fun listeners(){
        binding.edTitle.addTextChangedListener {
            val title = binding.edTitle.text.toString()
            viewModel.onEvent(EditTestFormEvents.TitleChanged(title))
        }

        binding.editTextVariant1.addTextChangedListener {
            viewModel.onEvent(EditTestFormEvents.Variant1(binding.editTextVariant1.text.toString()))
        }
        binding.editTextVariant2.addTextChangedListener {
            viewModel.onEvent(EditTestFormEvents.Variant2(binding.editTextVariant2.text.toString()))
        }
        binding.editTextVariant3.addTextChangedListener {
            viewModel.onEvent(EditTestFormEvents.Variant3(binding.editTextVariant3.text.toString()))
        }
        binding.editTextVariant4.addTextChangedListener {
            viewModel.onEvent(EditTestFormEvents.Variant4(binding.editTextVariant4.text.toString()))
        }

        listInputLayouts.forEachIndexed { index, textInputLayout ->
            textInputLayout.setEndIconOnClickListener {
                viewModel.onEvent(EditTestFormEvents.CheckChanged(index))
            }
        }
        binding.btnAdd.setOnClickListener {
            viewModel.onEvent(EditTestFormEvents.Submit)
        }
        binding.btnClose.setOnClickListener {
            viewModel.onEvent(EditTestFormEvents.Cancel)
            parentFragmentManager.popBackStack()
        }
    }

    override fun onDestroy() {
        APP.viewModel.uiState(MainActivityUiState.CloseModalFragment)
        super.onDestroy()
    }
}