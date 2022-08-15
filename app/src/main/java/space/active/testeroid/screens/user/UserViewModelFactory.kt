package space.active.testeroid.screens.user

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.RepositoryRealization
import space.active.testeroid.db.TestsDatabase

class UserViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: RepositoryRealization = RepositoryRealization(dao)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return UserViewModel(repository) as T
    }

}