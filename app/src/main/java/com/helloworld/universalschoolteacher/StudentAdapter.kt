package com.helloworld.universalschoolteacher

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.helloworld.universalschoolteacher.model.Student

class StudentAdapter(private val students: MutableList<Student>) :
    RecyclerView.Adapter<StudentAdapter.StudentViewHolder>() {

    inner class StudentViewHolder(inflater: LayoutInflater, parent: ViewGroup) :
        RecyclerView.ViewHolder(inflater.inflate(R.layout.item_student, parent, false)) {

        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val marksEditText: EditText = itemView.findViewById(R.id.marksEditText)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return StudentViewHolder(inflater, parent)
    }

    override fun onBindViewHolder(holder: StudentViewHolder, position: Int) {
        val student = students[position]
        holder.nameTextView.text = student.name
        holder.marksEditText.setText(student.marks?.toString() ?: "")

        holder.marksEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                val mark = s.toString().toIntOrNull()
                students[holder.adapterPosition].marks = mark ?: 0
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    override fun getItemCount(): Int = students.size

    fun getUpdatedStudents(): List<Student> = students
}
