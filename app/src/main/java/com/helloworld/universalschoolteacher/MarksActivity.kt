package com.helloworld.universalschoolteacher

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MarksActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var className: String

    private lateinit var etTestName: EditText
    private lateinit var etTotalMarks: EditText
    private lateinit var btnCreate: Button
    private lateinit var studentListLayout: LinearLayout
    private lateinit var btnUpload: Button

    private val marksInputMap = mutableMapOf<String, EditText>()
    private var testName = ""
    private var totalMarks = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks)

        className = intent.getStringExtra("className") ?: "EIGHT"
        database = FirebaseDatabase.getInstance().reference

        etTestName = findViewById(R.id.etTestsName)
        etTotalMarks = findViewById(R.id.etTotalsMarks)
        btnCreate = findViewById(R.id.btnCreateTest)
        studentListLayout = findViewById(R.id.studentListLayout)
        btnUpload = findViewById(R.id.saveMarksButton)

        btnCreate.setOnClickListener {
            val name = etTestName.text.toString().trim()
            val total = etTotalMarks.text.toString().trim()

            if (name.isEmpty() || total.isEmpty()) {
                Toast.makeText(this, "Fill both fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            testName = name
            totalMarks = total

            // Hide input and show student layout
            etTestName.visibility = View.GONE
            etTotalMarks.visibility = View.GONE
            btnCreate.visibility = View.GONE

            studentListLayout.visibility = View.VISIBLE
            btnUpload.visibility = View.VISIBLE

            fetchStudents()
        }

        btnUpload.setOnClickListener {
            uploadMarks()
        }
    }

    private fun fetchStudents() {
        Log.d("FirebasePath", "students/$className")

        val ref = database.child("students").child(className)

        ref.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                studentListLayout.removeAllViews()
                marksInputMap.clear()

                for (studentSnap in snapshot.children) {
                    val studentKey = studentSnap.key ?: continue
                    val name = studentSnap.child("Name").getValue(String::class.java) ?: "Unnamed"

                    val row = LinearLayout(this@MarksActivity).apply {
                        orientation = LinearLayout.HORIZONTAL
                        layoutParams = LinearLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        ).apply {
                            setMargins(0, 16, 0, 0)
                        }
                    }

                    val nameView = TextView(this@MarksActivity).apply {
                        text = name
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }

                    val input = EditText(this@MarksActivity).apply {
                        hint = "Marks"
                        inputType = InputType.TYPE_CLASS_NUMBER
                        layoutParams = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f)
                    }

                    row.addView(nameView)
                    row.addView(input)
                    studentListLayout.addView(row)

                    marksInputMap[studentKey] = input
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MarksActivity, "Failed to load students", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun uploadMarks() {
        // Example: current month as key
        val month = SimpleDateFormat("MMMM", Locale.getDefault()).format(Date())

        // Base reference: TestReports -> Class -> Month -> TestName
        val baseRef = database.child("TestReports").child(className).child(month).child(testName)

        val studentsMap = mutableMapOf<String, Any>()
        for ((studentKey, input) in marksInputMap) {
            val marksText = input.text.toString()
            val marks = marksText.toIntOrNull()

            if (marks == null) {
                input.error = "Enter valid number"
                continue
            }

            // Replace this with actual student name if available
            val studentName = studentKey

            val sanitizedKey = studentKey.replace(".", "_")
            studentsMap[sanitizedKey] = mapOf(
                "Name" to studentName,
                "ObtainedMarks" to marks
            )
        }

        // Build full test report object
        val testReport = mapOf(

            "TestName" to testName,
            "TotalMarks" to totalMarks,
            "Students" to studentsMap
        )

        baseRef.setValue(testReport).addOnSuccessListener {
            Toast.makeText(this, "Marks uploaded successfully", Toast.LENGTH_SHORT).show()

            // ----- SEND NOTIFICATION -----
            FcmSender.sendNotificationToClass(
                context = this,
                title = "Test Marks Uploaded",
                body = "Marks for '$testName' have been uploaded for your class!"
            )
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to upload marks", Toast.LENGTH_SHORT).show()
        }
    }

}
