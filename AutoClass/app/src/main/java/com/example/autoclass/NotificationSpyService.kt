package com.example.autoclass

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import java.util.regex.Pattern

class NotificationSpyService : NotificationListenerService() {

    private val TAG = "AutoClassSpy"

    override fun onListenerConnected() {
        super.onListenerConnected()
        Log.d(TAG, "Spy Service Connected! Ready.")
    }

   override fun onNotificationPosted(sbn: StatusBarNotification) {
        // 1. DISABLE THIS FILTER FOR TESTING:
        // if (sbn.packageName == "com.facebook.orca") { 
            
            val extras = sbn.notification.extras
            val text = extras.getCharSequence(Notification.EXTRA_TEXT)?.toString() ?: ""
            
            // Log the package name so you can see who sent it
            Log.d(TAG, "Notification from ${sbn.packageName}: $text")
            
            checkForLinks(text)
            
        // } // Don't forget to comment out the closing brace too!
    }

    private fun checkForLinks(message: String) {
        // Regex for Zoom and Google Meet links
        val urlPattern = Pattern.compile(
            "(https?:\\/\\/(?:zoom\\.us\\/j\\/\\d+|meet\\.google\\.com\\/[a-z]{3}-[a-z]{4}-[a-z]{3}))",
            Pattern.CASE_INSENSITIVE
        )
        val matcher = urlPattern.matcher(message)

        if (matcher.find()) {
            val foundLink = matcher.group(1)
            Log.e(TAG, "🚨 NEW CLASS LINK FOUND: $foundLink")
        }
    }
}