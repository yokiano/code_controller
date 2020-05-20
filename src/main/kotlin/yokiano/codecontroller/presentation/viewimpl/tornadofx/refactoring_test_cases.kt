package yokiano.codecontroller.presentation.viewimpl.tornadofx

data class RefactorTest(
    val name: String,
    val value: Any,
    val id: String,
    val declarationString: String,
    val input: String,
    val output: String
)

class RefactoringTests() {

    val caseList = listOf(
        RefactorTest(
            "basic", 3.0, "test id", "ccDouble",
            "val someVal = ccDouble(\"test id\")",
            "val someVal = 3.0"
        ),
        RefactorTest(
            "truncate number", 3.123456789, "test id", "ccDouble",
            "val someVal = ccDouble(\"test id\")",
            "val someVal = 3.123"
        ),
        RefactorTest(
            "conf block one liner", 3.0, "test id", "ccDouble",
            "val someVal = ccDouble(\"test id\") { range = 3.0..5.3 }",
            "val someVal = 3.0"
        ),
        RefactorTest(
            "ccVec with conf - parenthesis in conf block", Pair(30.0,30.0) , "test id", "ccVec",
            "val someVal = ccVec2(\"test id\") { setRange(30.0, 30.0, 100.0, 100.0) }",
            "val someVal = Pair(30.0,30.0)"
        )


    )
}