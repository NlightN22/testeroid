package space.active.testeroid.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import space.active.testeroid.db.dao.TestsDao
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.db.modelsdb.Users
import space.active.testeroid.db.relations.TestWithQuestions
import kotlinx.coroutines.flow.Flow

class RepositoryRealization(private val dao: TestsDao): Repository {
    override fun allUsers(): LiveData<List<Users>> {
        return dao.getAllUsers()
    }

    override suspend fun addUser(user: Users) {
        dao.insertUser(user)
    }

    override suspend fun getUser(userId: Long): Users {
        return dao.getUser(userId)
    }

    override suspend fun getUserScore(userId: Long): Int {
        return getUser(userId).score
    }

    override suspend fun deleteUser(user: Users) {
        dao.deleteUser(user)
    }

    override fun getAllTests(): LiveData<List<Tests>> {
        return dao.getAllTestsOnly()
    }

    override suspend fun addNewTestWithQuestions(test: Tests, questions: List<Questions>) {
        dao.addNewTestWithQuestions(test, questions)
    }

    override suspend fun getTest(testId: Long): Tests {
        return dao.getTest(testId)
    }

    override fun allTestsWithQuestions(): LiveData<List<TestWithQuestions>> {
        return dao.getAllTestsWithQuestions()
    }

    override fun getTestWithQuestionsFlow(testId: Long): Flow<TestWithQuestions> {
        return dao.getTestWithQuestionsFlow(testId)
    }

    override suspend fun deleteTestWithQuestions(testId: Long) {
        val test =  dao.getTestWithQuestions(testId)
        val tests = test.tests
        val questions = test.questions
        dao.deleteTestWithQuestions(tests, questions)
    }
}