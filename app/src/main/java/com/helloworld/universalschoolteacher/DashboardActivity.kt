package com.helloworld.universalschoolteacher

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class DashboardActivity : AppCompatActivity() {

    private lateinit var diaryBtn: Button
    private lateinit var testReportBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Setup Toolbar
        val toolbar: Toolbar = findViewById(R.id.toolbarDashboard)
        setSupportActionBar(toolbar)

        diaryBtn = findViewById(R.id.btnDiary)
        testReportBtn = findViewById(R.id.btnTestReport)

        diaryBtn.setOnClickListener {
            val intent = Intent(this, DiaryActivity::class.java)

            // If you want to pass the className that the user manages:
            val sharedPrefs = getSharedPreferences("MyPrefs", MODE_PRIVATE)
            val className = sharedPrefs.getString("className", "") ?: ""
            intent.putExtra("className", className)

            startActivity(intent)        }

        testReportBtn.setOnClickListener {
// Open MarksActivity and pass the className
            val intent = Intent(this, MarksActivity::class.java)

            // Pass class name if needed
            val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
            val className = prefs.getString("className", "UnknownClass")
            intent.putExtra("className", className)

            startActivity(intent)        }
    }

    // Inflate menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_dashboard, menu)
        return true
    }

    // Handle menu item click
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                // Clear saved login
                val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                prefs.edit().clear().apply()

                // Go back to login
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
