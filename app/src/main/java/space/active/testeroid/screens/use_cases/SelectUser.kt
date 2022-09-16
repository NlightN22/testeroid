package space.active.testeroid.screens.use_cases

import android.util.Log
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.repository.DataBaseRepository
import space.active.testeroid.repository.DataStoreRepository

class SelectUser (
    private val repository: DataBaseRepository,
    private val dataStore: DataStoreRepository
        )
{
    suspend operator fun invoke(userId: Long): Users {
        Log.e(TAG, "class SelectUser userId: $userId")
        try {
            val user = repository.getUser(userId)
            dataStore.saveSelectedUser(user)
            return user
        } catch (e: Exception) {
            Log.e(TAG, "{e.message}")
        }
        return Users()
    }
}