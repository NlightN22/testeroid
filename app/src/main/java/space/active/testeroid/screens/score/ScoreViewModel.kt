package space.active.testeroid.screens.score

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.helpers.notifyObserver
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataBaseRepository


class ScoreViewModel(
    private val dataBaseRepository: DataBaseRepository,
    private val dataStore: DataStoreRepository
    ): ViewModel() {

    val userIdFlow: Flow<Long?> = dataStore.userId
    private var _userScore = MutableStateFlow("")
    val userScore: StateFlow<String> = _userScore

    private var _currentUser: Users = Users()

    private val _formState = MutableLiveData<ScoreFormState>(ScoreFormState())
    val formState: LiveData<ScoreFormState> = _formState

    fun uiState(state: ScoreUiState) {
        when (state) {
            is ScoreUiState.UserScore -> {
                val selectedUserId =  state.userId
                Log.e(TAG, "selectedUserId: $selectedUserId ")
                selectedUserId?.let {
                    viewModelScope.launch {
                        _currentUser = dataBaseRepository.getUser(selectedUserId)
                        _currentUser?.let { user ->
                            getUserScore(user.userId)
                            _formState.value?.let { form ->
                                form.title = true
                                form.username = _currentUser.userName.uppercase()
//                            form.score = _currentUser.score.toString()
                                form.paramsVisibility = _currentUser.userAdministrator
                                _formState.notifyObserver()
                                if (_currentUser.userAdministrator) {
                                    uiState(ScoreUiState.UpdateParams)
                                }
                            }
                        }?: run {
                            _formState.value?.let { form ->
                                form.title = false
                                form.paramsVisibility = false
                            }
                        }
                    }
                }
            }
            is ScoreUiState.UpdateParams -> {
                viewModelScope.launch {
                    _formState.value?.let { form ->
                        val correct = dataStore.correctScore.first()
                        correct.let {
                            form.correctScore = it.toString()
                        }
                        val notCorrect = dataStore.notCorrectScore.first()
                        notCorrect.let {
                            form.notCorrectScore = it.toString()
                        }
                    }
                }
            }
            is ScoreUiState.Empty -> {
                _formState.value?.let {form ->
                    form.title = false
                    _formState.notifyObserver()
                }
            }
        }
    }

    fun onEvent(event: ScoreFormEvents) {
        when (event){
            is ScoreFormEvents.SubmitParams -> {
                val correct: Int? = event.correct.toIntOrNull()
                val notCorrect: Int? = event.notCorrect.toIntOrNull()
                viewModelScope.launch {
                    correct?.let { dataStore.saveCorrectScore(it) }
                    notCorrect?.let { dataStore.saveNotCorrectScore(it) }
                    uiState(ScoreUiState.UpdateParams)
                }
            }
        }
    }

    private fun getUserScore(userId: Long?) {
        viewModelScope.launch {
            userId?.let { userId->
                dataBaseRepository.getUserScoreFlow(userId).collectLatest { score->
                    _userScore.emit(score.toString())
                }
            }
        }
    }
}