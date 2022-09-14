package space.active.testeroid.repository

import kotlinx.coroutines.flow.Flow
import space.active.testeroid.db.modelsdb.Users

interface DataStoreRepository {
    val userId: Flow<Long?>
    val admin: Flow<Boolean>
    val correctScore: Flow<Int>
    val notCorrectScore: Flow<Int>
    val firstStart: Flow<Boolean>

    suspend fun saveSelectedUser(user: Users)
    suspend fun saveCorrectScore(score: Int)
    suspend fun saveNotCorrectScore(score: Int)
    suspend fun saveFirstStart(first: Boolean)
    suspend fun clearDataStore()
}