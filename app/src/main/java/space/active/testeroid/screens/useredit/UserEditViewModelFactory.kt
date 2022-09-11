package space.active.testeroid.screens.useredit

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.TestsDatabase
import space.active.testeroid.repository.DataStoreRepository

class UserEditViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: RepositoryRealization = RepositoryRealization(dao)
    private val dataStore: DataStoreRepository = DataStoreRepository(context)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserEditViewModel(repository, dataStore) as T
    }

}