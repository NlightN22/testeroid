package space.active.testeroid.repository

import androidx.lifecycle.LiveData
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.db.relations.TestWithQuestions
import kotlinx.coroutines.flow.Flow

interface DataBaseRepository {

    // User
    fun allUsers(): Flow<List<Users>>
    suspend fun addUser(user:Users)
    suspend fun getUser(userId: Long): Users
    fun getUserFlow(userId: Long): Flow<Users>
    suspend fun getUserScore(userId: Long): Int
    fun getUserScoreFlow(userId: Long): Flow<Int>
    suspend fun deleteUser(user: Users)

    // Test
    fun getAllTests():Flow<List<Tests>>
    suspend fun addNewTestWithQuestions(test: Tests, questions: List<Questions>)
    suspend fun getTest(testId: Long): Tests

    // TestWithQuestions
    fun allTestsWithQuestions(): LiveData<List<TestWithQuestions>>
    fun getTestWithQuestionsFlow(testId: Long): Flow<TestWithQuestions>
    suspend fun deleteTestWithQuestions(testId: Long)

}