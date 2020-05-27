package com.example.notificationsync

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build





class BootBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val service = Intent(context, MyNotificationListenerService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(service)
        } else {
            context.startService(service)
        }
    }

    companion object {
    }
}