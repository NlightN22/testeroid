package space.active.testeroid.db.relations

import androidx.room.Embedded
import androidx.room.Relation
import space.active.testeroid.db.modelsdb.Questions
import space.active.testeroid.db.modelsdb.Tests

data class TestWithQuestions (
    @Embedded val tests: Tests,
    @Relation(
        parentColumn = "testId",
        entityColumn = "testIdMain"
    )
    var questions: List<Questions>
)
