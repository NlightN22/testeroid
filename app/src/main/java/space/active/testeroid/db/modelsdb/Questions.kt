package space.active.testeroid.db.modelsdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Questions(
    @PrimaryKey(autoGenerate = true)
    val questionId: Long = 0,
    @ColumnInfo
    var testIdMain: Long = 0,
    @ColumnInfo
    val questionName: String = "empty",
    @ColumnInfo
    var correctAnswer: Boolean = false,
)
