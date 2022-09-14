package space.active.testeroid.screens.score

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.collectLatest
import space.active.testeroid.R
import space.active.testeroid.TAG
import space.active.testeroid.databinding.FragmentScoreBinding
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.repository.DataStoreRepositoryImplementation

class ScoreFragment : Fragment() {
    lateinit var binding: FragmentScoreBinding
    lateinit var viewModel: ScoreViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentScoreBinding.inflate(layoutInflater, container, false)
        viewModel = ViewModelProvider(
            this,
            ScoreViewModelFactory(this.requireContext()))
            .get(ScoreViewModel::class.java)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        init()
    }

    private fun init(){
        externalData()
        observers()
        listeners()
        Log.e(TAG, "ScoreFragment end initialization")
    }

    private fun externalData() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.userIdFlow.collect { userId ->
                viewModel.uiState(ScoreUiState.UserScore(userId))
            }
        }
    }

    private fun observers() {
        viewModel.formState.observe(viewLifecycleOwner){ form->
            if (form.title) {
                binding.textViewUserTitle.text = getString(R.string.score_user_title, form.username)
            } else {
                binding.textViewUserTitle.text = getString(R.string.score_user_empty)
            }
            binding.layoutScoreParams.isVisible = form.paramsVisibility
            binding.editTextCorrect.setText(form.correctScore)
            binding.editTextNotCorrect.setText(form.notCorrectScore)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.userScore.collectLatest {
                binding.textUserScore.text = it
            }
        }
    }

    private fun listeners() {
        binding.buttonSubmit.setOnClickListener {
            val correct = binding.editTextCorrect.text.toString()
            val notCorrect = binding.editTextNotCorrect.text.toString()

            viewModel.onEvent(ScoreFormEvents.SubmitParams(correct, notCorrect))
        }
    }

}

class ScoreViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: DataBaseRepositoryRealization = DataBaseRepositoryRealization(dao)
    private val dataStore: DataStoreRepository = DataStoreRepositoryImplementation(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ScoreViewModel(repository, dataStore) as T
    }

}