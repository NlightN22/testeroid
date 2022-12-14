package space.active.testeroid.screens.main

sealed class MainActivityUiState {
    object HideBottom: MainActivityUiState()
    object ShowTabs: MainActivityUiState ()
    data class ShowNavigation( var navigation: MainActivityFormState.Navigation): MainActivityUiState ()
    object ShowModalFragment: MainActivityUiState()
    object CloseModalFragment: MainActivityUiState()
}
