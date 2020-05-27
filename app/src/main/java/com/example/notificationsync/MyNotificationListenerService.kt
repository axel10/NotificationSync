package com.example.notificationsync

import android.app.Notification
import android.os.Build
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.KITKAT)
class MyNotificationListenerService : NotificationListenerService() {
    override fun onNotificationPosted(sbn: StatusBarNotification?) {
        if (sbn != null) {
            Log.d("receive a message",sbn.packageName)
            if (StorageUtils.isExistApp(applicationContext, sbn.packageName)) {
                if (!StorageUtils.checkSettingIsComplete(applicationContext)) return
                val receiverMail =
                    StorageUtils.getSetting(applicationContext, SettingKey.ReceiverMail)
                val senderMail = StorageUtils.getSetting(applicationContext, SettingKey.SenderMail)
                val password = StorageUtils.getSetting(applicationContext, SettingKey.Password)
                val smtp = StorageUtils.getSetting(applicationContext, SettingKey.SMTP)
                val port = StorageUtils.getSetting(applicationContext, SettingKey.Port)
                val title =
                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TITLE).toString()
                val content =
                    sbn.notification.extras.getCharSequence(Notification.EXTRA_TEXT).toString()
                MailUtils.sendMail(
                    "你的手机收到了一条重要消息",
                    receiverMail!!,
                    senderMail!!,
                    title + "\n" + content,
                    senderMail,
                    password!!,
                    smtp!!, port!!.toInt()
                )
            }
        }
        super.onNotificationPosted(sbn)
    }

}