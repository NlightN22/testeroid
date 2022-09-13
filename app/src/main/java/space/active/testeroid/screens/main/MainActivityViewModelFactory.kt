package space.active.testeroid.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataStoreRepositoryImplementation


class MainActivityViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dataStore: DataStoreRepository = DataStoreRepositoryImplementation(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(dataStore) as T
    }

}