import android.content.Context
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream

object FcmSender {

    private const val FCM_URL =
        "your_real_FCM_URL"

    // ----- Data classes for FCM payload -----
    data class Notification(val title: String, val body: String)
    data class AndroidNotification(
        val title: String,
        val body: String,
        val channel_id: String
    )
    data class Android(
        val priority: String,
        val notification: AndroidNotification
    )
    data class Message(val token: String, val android: Android)
    data class RootMessage(val message: Message)

    // ----- Send notification to a single token -----
    private fun sendNotificationToToken(
        context: Context,
        deviceToken: String,
        title: String,
        body: String
    ) {
        Thread {
            try {
                val inputStream: InputStream = context.assets.open("serviceAccount.json")
                val credentials = GoogleCredentials.fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/firebase.messaging"))
                credentials.refreshIfExpired()
                val accessToken = credentials.accessToken.tokenValue

                val message = RootMessage(
                    Message(
                        token = deviceToken,
                        android = Android(
                            priority = "high",
                            notification = AndroidNotification(
                                title = title,
                                body = body,
                                channel_id = "test_reports_channel"
                            )
                        )
                    )
                )

                val json = Gson().toJson(message)

                val client = OkHttpClient()
                val request = Request.Builder()
                    .url(FCM_URL)
                    .addHeader("Authorization", "Bearer $accessToken")
                    .addHeader("Content-Type", "application/json; UTF-8")
                    .post(json.toRequestBody("application/json; charset=utf-8".toMediaType()))
                    .build()

                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    println("✅ Notification sent successfully!")
                } else {
                    println("❌ Failed: ${response.body?.string()}")
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }.start()
    }

    // ----- Public function: send to all students in teacher's class -----
    fun sendNotificationToClass(context: Context, title: String, body: String) {
        val prefs = context.getSharedPreferences("login_prefs", Context.MODE_PRIVATE)
        val className = prefs.getString("className", null) ?: return

        val dbRef = FirebaseDatabase.getInstance().getReference("students/$className")
        dbRef.get().addOnSuccessListener { snapshot ->
            for (studentSnap in snapshot.children) {
                val token = studentSnap.child("fcmToken").getValue(String::class.java)
                if (!token.isNullOrEmpty()) {
                    sendNotificationToToken(context, token, title, body)
                }
            }
        }.addOnFailureListener {
            println("Failed to fetch tokens: ${it.message}")
        }
    }
}
