package space.active.testeroid.screens.test

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.TestsDatabase

class TestViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: RepositoryRealization = RepositoryRealization(dao)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return TestViewModel(repository) as T
    }

}