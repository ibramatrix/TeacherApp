package com.helloworld.universalschoolteacher

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // Fade animation (optional)
        val logo = findViewById<ImageView>(R.id.splashLogo)
        logo.alpha = 0f
        logo.animate().alpha(1f).setDuration(1500).start() // fade in

        // Wait 3-4 seconds and move to MainActivity
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, MainActivity::class.java))
            finish() // so splash activity is removed from back stack
        }, 3500) // 3.5 seconds
    }
}
