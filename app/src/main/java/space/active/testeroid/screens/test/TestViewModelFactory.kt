package space.active.testeroid.screens.test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository
import space.active.testeroid.repository.DataStoreRepositoryImplementation

class TestViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: DataBaseRepositoryRealization = DataBaseRepositoryRealization(dao)
    private val dataStore: DataStoreRepository = DataStoreRepositoryImplementation(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TestViewModel(repository, dataStore) as T
    }

}