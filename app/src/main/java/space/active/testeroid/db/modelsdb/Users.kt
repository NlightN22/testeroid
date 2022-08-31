package space.active.testeroid.db.modelsdb

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Users (
    @PrimaryKey(autoGenerate = true)
    var userId: Long = 0,
    @ColumnInfo
    var userName: String = "",
    @ColumnInfo
    var userPassword: String = "",
    @ColumnInfo
    var userAdministrator: Boolean = false,
    @ColumnInfo
    var score: Int = 0
)