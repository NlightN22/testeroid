package space.active.testeroid.screens.test

import space.active.testeroid.db.relations.TestWithQuestions

sealed class TestUiState {
    data class ShowFirst(val listTests: List<TestWithQuestions>): TestUiState()
    object ShowNext: TestUiState()
    data class ShowCorrect(val position: Int): TestUiState()
    object Final: TestUiState()
    object ShowEmpty: TestUiState()
}