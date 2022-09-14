package space.active.testeroid.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import space.active.testeroid.TAG
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.screens.main.dataStore
import java.io.IOException

const val SELECTED_USER = "userId"
const val SELECTED_ADMINISTRATOR = "userAdmin"
const val CORRECT_SCORE = "correctScore"
const val NOT_CORRECT_SCORE = "notCorrectScore"
const val FIRST_START = "firstStart"

class DataStoreRepositoryImplementation(context: Context): DataStoreRepository {
    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferenceKeys {
        val userId = longPreferencesKey(SELECTED_USER)
        val admin = booleanPreferencesKey(SELECTED_ADMINISTRATOR)
        val correctScore = intPreferencesKey(CORRECT_SCORE)
        val notCorrectScore = intPreferencesKey(NOT_CORRECT_SCORE)
        val firstStart = booleanPreferencesKey(FIRST_START)
    }

    private suspend fun saveUserId(userId: Long){
        dataStore.edit { preference ->
            preference[PreferenceKeys.userId] = userId
        }
    }

    override val userId: Flow<Long?> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.userId]
        }

    private suspend fun saveUserAdmin(value: Boolean){
        dataStore.edit { preference ->
            preference[PreferenceKeys.admin] = value
        }
    }

    override suspend fun saveSelectedUser(user: Users) {
        saveUserId(user.userId)
        saveUserAdmin(user.userAdministrator)
    }

    override val admin: Flow<Boolean> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.admin] ?: false
        }

    override suspend fun saveCorrectScore(score: Int){
        dataStore.edit { preference ->
            preference[PreferenceKeys.correctScore] = score
        }
    }
    override val correctScore: Flow<Int> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.correctScore] ?: 0
        }

    override suspend fun saveNotCorrectScore(score: Int){
        dataStore.edit { preference ->
            preference[PreferenceKeys.notCorrectScore] = score
        }
    }

    override suspend fun saveFirstStart(first: Boolean) {
        dataStore.edit { preference ->
            preference[PreferenceKeys.firstStart] = first
        }
    }

    override val notCorrectScore: Flow<Int> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.notCorrectScore] ?: 0
        }
    override val firstStart: Flow<Boolean> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.firstStart] ?: true
        }

    private fun exceptionHandling(exception: Throwable){
        if (exception is IOException){
            Log.e(TAG, "DataStoreRepository readFromDataStore: ${exception.message}")
        }else{
            throw exception
        }
    }

    override suspend fun clearDataStore(){
        dataStore.edit { it.clear() }
    }
}