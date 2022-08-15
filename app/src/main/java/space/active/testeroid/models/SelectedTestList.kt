package space.active.testeroid.models

import space.active.testeroid.db.modelsdb.Tests

data class SelectedTest(
    val selected: Boolean,
    val test: Tests)
