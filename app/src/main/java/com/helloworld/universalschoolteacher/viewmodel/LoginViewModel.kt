package com.helloworld.universalschoolteacher.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.database.FirebaseDatabase
import com.helloworld.universalschoolteacher.model.Staff

class LoginViewModel : ViewModel() {

    fun loginStaff(
        username: String,
        password: String,
        onSuccess: (manages: String) -> Unit,
        onFailure: (error: String) -> Unit
    ) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Stafflist")

        dbRef.get().addOnSuccessListener { snapshot ->
            for (staffSnapshot in snapshot.children) {
                val staff = staffSnapshot.getValue(Staff::class.java)
                if (staff != null &&
                    staff.Username.equals(username.trim(), ignoreCase = true) &&
                    staff.Password == password &&
                    staff.isActive == true
                ) {
                    onSuccess(staff.manages ?: "")
                    return@addOnSuccessListener
                }
            }
            onFailure("Invalid credentials or staff is not active")
        }.addOnFailureListener {
            onFailure("Firebase error: ${it.localizedMessage}")
        }
    }
}
