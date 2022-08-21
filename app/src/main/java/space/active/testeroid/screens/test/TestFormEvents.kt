package space.active.testeroid.screens.test

import space.active.testeroid.db.relations.TestWithQuestions

sealed class TestFormEvents {
    object Variant1: TestFormEvents()
    object Variant2: TestFormEvents()
    object Variant3: TestFormEvents()
    object Variant4: TestFormEvents()
    data class Restart(val listTests: List<TestWithQuestions>?): TestFormEvents()
}