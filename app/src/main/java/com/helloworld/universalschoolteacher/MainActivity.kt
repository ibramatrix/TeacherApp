package com.helloworld.universalschoolteacher

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var database: DatabaseReference
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check for saved login
        val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
        val savedUsername = prefs.getString("username", null)
        if (savedUsername != null) {
            // Move to dashboard
            startActivity(Intent(this, DashboardActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        usernameEditText = findViewById(R.id.editUsername)
        passwordEditText = findViewById(R.id.editPassword)
        loginBtn = findViewById(R.id.btnLogin)

        loginBtn.setOnClickListener {
            val username = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            loginUser(username, password)
        }
    }

    private fun loginUser(username: String, password: String) {
        database = FirebaseDatabase.getInstance().getReference("Stafflist")
        database.orderByChild("Username").equalTo(username)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (staffSnapshot in snapshot.children) {
                            val staffPassword = staffSnapshot.child("Password").getValue(String::class.java)
                            val isActive = staffSnapshot.child("isActive").getValue(Boolean::class.java) ?: false
                            val manages = staffSnapshot.child("Manages").getValue(String::class.java)

                            if (password == staffPassword) {
                                if (isActive) {
                                    // Save login details
                                    val prefs = getSharedPreferences("login_prefs", MODE_PRIVATE)
                                    prefs.edit().apply {
                                        putString("username", username)
                                        putString("className", manages)
                                        apply()
                                    }

                                    // Move to dashboard
                                    val intent = Intent(this@MainActivity, DashboardActivity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    Toast.makeText(this@MainActivity, "User is not active", Toast.LENGTH_SHORT).show()
                                }
                            } else {
                                Toast.makeText(this@MainActivity, "Incorrect password", Toast.LENGTH_SHORT).show()
                            }
                        }
                    } else {
                        Toast.makeText(this@MainActivity, "User not found", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
