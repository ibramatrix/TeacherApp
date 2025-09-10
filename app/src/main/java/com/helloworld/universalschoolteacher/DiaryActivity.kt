package com.helloworld.universalschoolteacher

import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.helloworld.universalschoolteacher.adapter.DiaryAdapter
import com.helloworld.universalschoolteacher.model.Diary

class DiaryActivity : AppCompatActivity() {

    private lateinit var rvDiaries: RecyclerView
    private lateinit var btnManage: Button
    private lateinit var btnCreate: Button
    private lateinit var diaryAdapter: DiaryAdapter
    private val diariesList = mutableListOf<Pair<String, Diary>>()
    private lateinit var dbRef: DatabaseReference
    private lateinit var className: String

    private lateinit var imagePickerLauncher: ActivityResultLauncher<String>
    var currentImageCallback: ((Uri) -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_diary)

        // Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarDiary)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Get className from prefs
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        className = prefs.getString("className", "") ?: ""

        rvDiaries = findViewById(R.id.rvDiaries)
        btnManage = findViewById(R.id.btnManageDiaries)
        btnCreate = findViewById(R.id.btnCreateDiary)

        rvDiaries.layoutManager = LinearLayoutManager(this)
        diaryAdapter = DiaryAdapter(this, diariesList, ::editDiary, ::deleteDiary)
        rvDiaries.adapter = diaryAdapter

        dbRef = FirebaseDatabase.getInstance().getReference("diaries")

        btnManage.setOnClickListener { loadDiaries() }
        btnCreate.setOnClickListener { createNewDiary() }

        // Image picker launcher
        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                currentImageCallback?.invoke(it)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun loadDiaries() {
        diariesList.clear()
        dbRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (child in snapshot.children) {
                    val diary = child.getValue(Diary::class.java)
                    val id = child.key ?: continue
                    if (diary?.ClassName == className) {
                        diariesList.add(Pair(id, diary))
                    }
                }
                diaryAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@DiaryActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun editDiary(id: String, diary: Diary) {
        showDiaryDialog(diary, id)
    }

    private fun deleteDiary(id: String) {
        dbRef.child(id).removeValue().addOnSuccessListener {
            Toast.makeText(this, "Deleted successfully", Toast.LENGTH_SHORT).show()
            loadDiaries()
        }.addOnFailureListener {
            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun createNewDiary() {
        showDiaryDialog(null, null)
    }

    private fun showDiaryDialog(existingDiary: Diary?, diaryId: String?) {
        val dialog = DiaryDialog(
            activity = this,
            className = className,
            existingDiary = existingDiary,
            diaryId = diaryId,
            onSuccess = { loadDiaries() },
            imagePickerLauncher = imagePickerLauncher
        )
        dialog.show()
    }
}
