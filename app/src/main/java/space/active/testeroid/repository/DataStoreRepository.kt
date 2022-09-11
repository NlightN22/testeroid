package space.active.testeroid.repository

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import space.active.testeroid.TAG
import space.active.testeroid.screens.main.dataStore
import java.io.IOException

const val SELECTED_USER = "userId"
const val CORRECT_SCORE = "correctScore"
const val NOT_CORRECT_SCORE = "notCorrectScore"

class DataStoreRepository(context: Context) {
    private val dataStore: DataStore<Preferences> = context.dataStore

    private object PreferenceKeys {
        val userId = longPreferencesKey(SELECTED_USER)
        val correctScore = intPreferencesKey(CORRECT_SCORE)
        val notCorrectScore = intPreferencesKey(NOT_CORRECT_SCORE)
    }

    suspend fun saveUserId(userId: Long){
        dataStore.edit { preference ->
            preference[PreferenceKeys.userId] = userId
        }
    }

    val userId: Flow<Long?> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.userId]
        }

    suspend fun saveCorrectScore(score: Int){
        dataStore.edit { preference ->
            preference[PreferenceKeys.correctScore] = score
        }
    }
    val correctScore: Flow<Int> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.correctScore] ?: 0
        }

    suspend fun saveNotCorrectScore(score: Int){
        dataStore.edit { preference ->
            preference[PreferenceKeys.notCorrectScore] = score
        }
    }

    val notCorrectScore: Flow<Int> = dataStore.data
        .catch { exception->
            exceptionHandling(exception)
            emit(emptyPreferences())
        }
        .map { preference ->
            preference[PreferenceKeys.notCorrectScore] ?: 0
        }

    private fun exceptionHandling(exception: Throwable){
        if (exception is IOException){
            Log.e(TAG, "DataStoreRepository readFromDataStore: ${exception.message}")
        }else{
            throw exception
        }
    }

    suspend fun clearDataStore(){
        dataStore.edit { it.clear() }
    }
}