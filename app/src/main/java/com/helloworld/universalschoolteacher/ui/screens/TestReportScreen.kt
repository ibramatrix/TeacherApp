package com.helloworld.universalschoolteacher.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import com.google.firebase.database.FirebaseDatabase
import com.helloworld.universalschoolteacher.model.Student

@Composable
fun TestReportScreen(modifier: Modifier = Modifier, className: String) {
    val context = LocalContext.current
    val database = FirebaseDatabase.getInstance().getReference("students").child(className)

    var testName by remember { mutableStateOf("") }
    var totalMarks by remember { mutableStateOf("") }
    var studentList by remember { mutableStateOf<Map<String, Student>>(emptyMap()) }
    var marksMap by remember { mutableStateOf(mutableMapOf<String, String>()) }

    var step by remember { mutableStateOf(0) } // 0 = initial, 1 = show students

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (step == 0) {
            OutlinedTextField(
                value = testName,
                onValueChange = { testName = it },
                label = { Text("Test Name") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = totalMarks,
                onValueChange = { totalMarks = it },
                label = { Text("Total Marks") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                if (testName.isBlank() || totalMarks.toIntOrNull() == null) {
                    Toast.makeText(context, "Fill all fields correctly", Toast.LENGTH_SHORT).show()
                } else {
                    database.get().addOnSuccessListener { snapshot ->
                        val map = snapshot.children.associate {
                            it.key!! to it.getValue(Student::class.java)!!
                        }
                        studentList = map
                        marksMap = map.mapValues { "" }.toMutableMap()
                        step = 1
                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to load students", Toast.LENGTH_SHORT).show()
                    }
                }
            }) {
                Text("Create")
            }
        }

        if (step == 1) {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .padding(top = 16.dp)
            ) {
                items(studentList.entries.toList()) { entry ->
                    val studentId = entry.key
                    val student = entry.value

                    Column(modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)) {
                        Text(text = "${student.name} (${studentId})")
                        OutlinedTextField(
                            value = marksMap[studentId] ?: "",
                            onValueChange = { newValue ->
                                if (newValue.all { it.isDigit() } || newValue.isEmpty()) {
                                    marksMap = marksMap.toMutableMap().apply {
                                        this[studentId] = newValue
                                    }
                                }
                            },
                            label = { Text("Obtained Marks") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Button(
                onClick = {
                    if (marksMap.any { it.value.toIntOrNull() == null || it.value.toInt() > (totalMarks.toIntOrNull() ?: Int.MAX_VALUE) }) {
                        Toast.makeText(context, "Please enter valid marks for all students", Toast.LENGTH_SHORT).show()
                        return@Button
                    }

                    val testReportsRef = FirebaseDatabase.getInstance()
                        .getReference("TestReports")
                        .child(className)

                    testReportsRef.get().addOnSuccessListener { snapshot ->
                        val nextTestId = "Test%03d".format(snapshot.childrenCount + 1)

                        val studentsFormatted = studentList.mapValues { (id, student) ->
                            mapOf(
                                "Name" to student.name,
                                "ObtainedMarks" to (marksMap[id]?.toIntOrNull() ?: 0)
                            )
                        }

                        val testReport = mapOf(
                            "TestName" to testName,
                            "TotalMarks" to totalMarks.toInt(),
                            "Students" to studentsFormatted
                        )

                        testReportsRef.child(nextTestId).setValue(testReport).addOnSuccessListener {
                            Toast.makeText(context, "✅ Test report saved to Firebase!", Toast.LENGTH_SHORT).show()
                            // Reset UI
                            step = 0
                            testName = ""
                            totalMarks = ""
                        }.addOnFailureListener {
                            Toast.makeText(context, "❌ Failed to save report", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(context, "❌ Error fetching existing reports", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text("Save Report")
            }
        }
    }
}
