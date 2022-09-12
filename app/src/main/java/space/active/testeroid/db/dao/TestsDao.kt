package space.active.testeroid.db.dao

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.room.*
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.db.relations.TestWithQuestions
import kotlinx.coroutines.flow.Flow
import space.active.testeroid.TAG

@Dao
interface TestsDao {

    // Add start

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTest(tests: Tests): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuestion(questions: List<Questions>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: Users)

    @Transaction
    suspend fun addNewTestWithQuestions(test: Tests, questions: List<Questions>) {
        // return ID from dao insert in Long
        val testId = insertTest(test)
        questions.forEach { it.testIdMain = testId }
        insertQuestion(questions)
        Log.e(TAG, "addNewTestWithQuestions: $test $testId $questions")
    }

    // Add end

    // Delete start

    @Delete
    suspend fun deleteTest(tests: Tests)

    @Delete
    suspend fun deleteQuestions(questions: List<Questions>)

    @Delete
    suspend fun deleteUser(user: Users)


    @Transaction
    suspend fun deleteTestWithQuestions(test: Tests, questions: List<Questions>){
//        val testWithQuestions: LiveData<TestWithQuestions> = getTestWithQuestions(testId)
//        testWithQuestions.value?.let {
//            val test: Tests = it.tests
//            val questions: List<Questions> = it.questions
            deleteTest(test)
            deleteQuestions(questions)
//        } ?: run {
//            Log.e(TAG, "Error in TestDao suspend fun deleteTestWithQuestions: $testWithQuestions")
//        }
    }

    // Delete end

    // Get start

    @Query("SELECT * FROM Questions WHERE testIdMain = :testIdMain")
    suspend fun getQuestions(testIdMain: Long): List<Questions>

    @Query("SELECT * FROM Tests WHERE testId = :testId")
    suspend fun getTest(testId: Long): Tests

    @Transaction
    @Query("SELECT * FROM Tests WHERE testId = :testId")
    fun getTestWithQuestionsFlow(testId: Long): Flow<TestWithQuestions>

    @Transaction
    @Query("SELECT * FROM Tests WHERE testId = :testId")
    suspend fun getTestWithQuestions(testId: Long): TestWithQuestions

    @Transaction
    @Query("SELECT * FROM Tests")
    fun getAllTestsWithQuestions(): LiveData<List<TestWithQuestions>>

    @Transaction
    @Query("SELECT * FROM Tests")
    fun getAllTestsOnly(): LiveData<List<Tests>>

    @Query("SELECT * FROM Users")
    fun getAllUsers(): Flow<List<Users>>

    @Query("SELECT * FROM Users WHERE userId = :userId")
    suspend fun getUser(userId: Long): Users

    @Query("SELECT * FROM Users WHERE userId = :userId")
    fun getUserFlow(userId: Long): Flow<Users>

    // Get end
}