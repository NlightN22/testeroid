package space.active.testeroid.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import space.active.testeroid.DATA_BASE_NAME
import space.active.testeroid.db.dao.TestsDao
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests
import space.active.testeroid.db.modelsdb.Users

@Database(
    entities = [
        Questions::class,
        Tests::class,
        Users::class,
    ],
    version = 2, exportSchema = true,
//    autoMigrations = [ AutoMigration(from = 1, to = 2)]
)
abstract class TestsDatabase: RoomDatabase() {

    abstract val testsDao: TestsDao

    companion object {
        private var INSTANCE: TestsDatabase? = null

        @Synchronized
        fun getInstance(context: Context): TestsDatabase {
            return if (INSTANCE == null){
                INSTANCE = Room.databaseBuilder(
                    context,
                    TestsDatabase::class.java,
                    "$DATA_BASE_NAME")
//                    .fallbackToDestructiveMigration() // uncheck for delete Database
                    .build()
                INSTANCE as TestsDatabase
            }else{
                INSTANCE as TestsDatabase
            }
        }
    }
}
