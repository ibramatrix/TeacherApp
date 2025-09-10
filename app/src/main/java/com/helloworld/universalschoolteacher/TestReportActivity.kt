package com.helloworld.universalschoolteacher

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.FirebaseDatabase
import com.helloworld.universalschoolteacher.model.Student
import com.helloworld.universalschoolteacher.model.TestReport
import com.helloworld.universalschoolteacher.model.TestStudentRecord

class TestReportActivity : AppCompatActivity() {

    private lateinit var editTestName: EditText
    private lateinit var editTotalMarks: EditText
    private lateinit var btnCreateTest: Button
    private lateinit var btnSaveReport: Button
    private lateinit var recyclerStudents: RecyclerView

    private lateinit var adapter: StudentAdapter
    private val studentList = mutableListOf<Student>()

    private var managesClass: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_report)

        editTestName = findViewById(R.id.editTestName)
        editTotalMarks = findViewById(R.id.editTotalMarks)
        btnCreateTest = findViewById(R.id.btnCreateTest)
        btnSaveReport = findViewById(R.id.btnSaveReport)
        recyclerStudents = findViewById(R.id.recyclerStudents)

        managesClass = intent.getStringExtra("MANAGES")

        adapter = StudentAdapter(studentList)
        recyclerStudents.layoutManager = LinearLayoutManager(this)
        recyclerStudents.adapter = adapter

        btnCreateTest.setOnClickListener {
            loadStudentsFromFirebase()
        }

        btnSaveReport.setOnClickListener {
            val testName = editTestName.text.toString()
            val totalMarks = editTotalMarks.text.toString().toIntOrNull()

            if (testName.isEmpty() || totalMarks == null) {
                Toast.makeText(this, "Enter valid test name and marks", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val report = TestReport(
                testName,
                totalMarks,
                studentList.associate {
                    it.name!! to TestStudentRecord(it.name!!, it.marks ?: 0)
                }
            )

            FirebaseDatabase.getInstance().getReference("TestReports")
                .child(managesClass ?: "UnknownClass")
                .child(testName)
                .setValue(report)
                .addOnSuccessListener {
                    Toast.makeText(this, "Report saved!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save report", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun loadStudentsFromFirebase() {
        val classId = managesClass ?: return

        FirebaseDatabase.getInstance().getReference("students/$classId")
            .get().addOnSuccessListener { snapshot ->
                studentList.clear()
                for (child in snapshot.children) {
                    val student = child.getValue(Student::class.java)
                    student?.let { studentList.add(it) }
                }
                adapter.notifyDataSetChanged()
                recyclerStudents.visibility = View.VISIBLE
                btnSaveReport.visibility = View.VISIBLE
            }.addOnFailureListener {
                Toast.makeText(this, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
    }
}
