package space.active.testeroid.screens.main

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository


class MainActivityViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dataStore: DataStoreRepository = DataStoreRepository(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MainActivityViewModel(dataStore) as T
    }

}