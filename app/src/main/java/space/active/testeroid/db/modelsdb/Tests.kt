package space.active.testeroid.db.modelsdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Tests (
    @PrimaryKey(autoGenerate = true)
    val testId: Long = 0,
    @ColumnInfo
    val testName: String =  "empty"
)
// Serializable need to work sending serializable data to fragment