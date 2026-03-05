package com.example.autoclass

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class AlarmActivity : AppCompatActivity() {

    // Default Zoom link (we will make this dynamic later)
    private val DEFAULT_LINK = "https://zoom.us/j/123456789"
    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 1. WAKE UP THE SCREEN (The Magic Code)
        turnScreenOnAndKeyguardOff()
        
        setContentView(R.layout.activity_alarm)

        // 2. LOGIC: Wait 3 seconds, then launch Zoom
        val statusText = findViewById<TextView>(R.id.statusText)
        val cancelBtn = findViewById<Button>(R.id.btnCancel)

        val runnable = Runnable {
            statusText.text = "🚀 Launching Zoom..."
            launchZoom(DEFAULT_LINK)
            finish() // Close this screen
        }

        // Schedule the launch
        handler.postDelayed(runnable, 3000)

        // Allow user to cancel if they are awake
        cancelBtn.setOnClickListener {
            handler.removeCallbacks(runnable)
            statusText.text = "❌ Auto-Join Cancelled"
            finish()
        }
    }

    private fun turnScreenOnAndKeyguardOff() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
            )
        }

        with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
            requestDismissKeyguard(this@AlarmActivity, null)
        }
    }

    private fun launchZoom(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        try {
            startActivity(intent)
        } catch (e: Exception) {
            // Zoom not installed? Open browser instead.
            val webIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
            startActivity(webIntent)
        }
    }
}