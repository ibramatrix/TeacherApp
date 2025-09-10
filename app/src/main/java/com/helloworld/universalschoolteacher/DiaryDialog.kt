package com.helloworld.universalschoolteacher

import android.app.Activity
import android.app.AlertDialog
import android.net.Uri
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.helloworld.universalschoolteacher.model.Diary
import java.text.SimpleDateFormat
import java.util.*
import androidx.activity.result.ActivityResultLauncher

class DiaryDialog(
    private val activity: Activity,
    private val className: String,
    private val existingDiary: Diary?,
    private val diaryId: String?,
    private val onSuccess: () -> Unit,
    private val imagePickerLauncher: ActivityResultLauncher<String>
) {

    private var selectedImageUri: Uri? = null
    private lateinit var dialog: AlertDialog

    fun show() {
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.dialog_diary, null)
        val etText = dialogView.findViewById<EditText>(R.id.etDiaryText)
        val ivImage = dialogView.findViewById<ImageView>(R.id.ivDiaryImage)
        val btnPickImage = dialogView.findViewById<Button>(R.id.btnPickImage)
        val btnSave = dialogView.findViewById<Button>(R.id.btnSaveDiary)

        // Pre-fill existing diary
        existingDiary?.let { diary ->
            etText.setText(diary.Text)
            val images = diary.Images
            if (!images.isNullOrEmpty()) {
                Glide.with(activity).load(images[0]).into(ivImage)
            }
        }

        // Image picker button
        btnPickImage.setOnClickListener {
            imagePickerLauncher.launch("image/*")  // Open gallery
        }

        // Save button
        btnSave.setOnClickListener {
            val text = etText.text.toString().trim()
            if (text.isEmpty()) {
                Toast.makeText(activity, "Enter diary text", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val dbRef = FirebaseDatabase.getInstance().getReference("diaries")
            val storageRef = FirebaseStorage.getInstance().getReference("diary_images")

            fun saveDiary(imageUrl: String?) {
                val formattedText = text.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


                val diary = Diary(
                    ClassName = className,
                    Date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                    Text = text,
                    Images = imageUrl?.let { listOf(it) } ?: existingDiary?.Images
                )

                if (diaryId != null) {
                    dbRef.child(diaryId).setValue(diary).addOnSuccessListener {
                        Toast.makeText(activity, "Diary updated!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onSuccess()
                    }
                } else {
                    dbRef.push().setValue(diary).addOnSuccessListener {
                        Toast.makeText(activity, "Diary created!", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        onSuccess()

                        // ----- SEND NOTIFICATION -----
                        FcmSender.sendNotificationToClass(
                            context = activity,
                            title = "New Diary Entry",
                            body = "A new diary has been posted for your class!"
                        )
                    }
                }
            }

            // Upload image if selected
            if (selectedImageUri != null) {
                val fileRef = storageRef.child(UUID.randomUUID().toString())
                fileRef.putFile(selectedImageUri!!).addOnSuccessListener {
                    fileRef.downloadUrl.addOnSuccessListener { uri ->
                        saveDiary(uri.toString())
                    }
                }.addOnFailureListener {
                    Toast.makeText(activity, "Image upload failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                saveDiary(null)
            }
        }

        dialog = AlertDialog.Builder(activity)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.show()

        // **Important: register callback for this dialog instance**
        (activity as? DiaryActivity)?.currentImageCallback = { uri ->
            selectedImageUri = uri
            Glide.with(activity).load(uri).into(ivImage)
        }
    }
}
