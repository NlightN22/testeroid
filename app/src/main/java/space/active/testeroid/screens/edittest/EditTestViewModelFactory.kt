package space.active.testeroid.screens.edittest

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import space.active.testeroid.repository.DataBaseRepositoryRealization
import space.active.testeroid.db.TestsDatabase

class EditTestViewModelFactory(context: Context): ViewModelProvider.Factory {

    private val dao = TestsDatabase.getInstance(context).testsDao
    private val repository: DataBaseRepositoryRealization = DataBaseRepositoryRealization(dao)

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return EditTestViewModel(repository) as T
    }

}