package com.helloworld.universalschoolteacher.model

data class TestReport(
    var testName: String = "",
    var totalMarks: Int = 0,
    var students: Map<String, TestStudentRecord> = emptyMap()
)
