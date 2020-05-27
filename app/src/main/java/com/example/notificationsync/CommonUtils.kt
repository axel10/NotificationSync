package com.example.notificationsync

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.widget.Toast

enum class GetAppInfoSign {
    ALL, SYSTEM, THIRD_PART
}

object CommonUtils {

    private const val ENABLED_NOTIFICATION_LISTENERS = "enabled_notification_listeners"

/*    fun showDialog(context: Context, title: String, message: String,listener:DialogInterface.OnClickListener?) {
        val dialog = AlertDialog.Builder(context)
        dialog.setTitle(title)
        dialog.setMessage(message)
        dialog.setNegativeButton(
            "确定"
        ) { _, _ ->
        }
        dialog.create().show()
    }*/

    fun isNotificationListenersEnabled(context: Context): Boolean {
        val pkgName = context.packageName
        val flat =
            Settings.Secure.getString(context.contentResolver, ENABLED_NOTIFICATION_LISTENERS)
        if (!TextUtils.isEmpty(flat)) {
            val names = flat.split(":")
            for (name in names) {
                val cn = ComponentName.unflattenFromString(name)
                if (cn != null) {
                    if (TextUtils.equals(pkgName, cn.packageName)) {
                        return true
                    }
                }
            }
        }
        return false
    }

    fun gotoNotificationAccessSetting(context: Context): Boolean {
        return try {
            val intent = Intent("android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            true
        } catch (e: ActivityNotFoundException) { //普通情况下找不到的时候需要再特殊处理找一次
            try {
                val intent = Intent()
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                val cn = ComponentName(
                    "com.android.settings",
                    "com.android.settings.Settings\$NotificationAccessSettingsActivity"
                )
                intent.component = cn
                intent.putExtra(":settings:show_fragment", "NotificationAccessSettings")
                context.startActivity(intent)
                return true
            } catch (e1: Exception) {
                e1.printStackTrace()
            }
            Toast.makeText(context, "对不起，您的手机暂不支持", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
            false
        }
    }

    fun toggleNotificationListenerService(context: Context) {
        Log.e("", "toggleNotificationListenerService")
        val pm = context.packageManager
        pm.setComponentEnabledSetting(
            ComponentName(context, MyNotificationListenerService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP
        )
        pm.setComponentEnabledSetting(
            ComponentName(context, MyNotificationListenerService::class.java),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP
        )
    }

    fun getAppInfo(context: Context, sign: GetAppInfoSign): MutableList<AppItem> {
        val appList: MutableList<AppItem> =
            mutableListOf() //用来存储获取的应用信息数据　　　　　
        val packages: List<PackageInfo> =
            context.packageManager.getInstalledPackages(0)
        for (i in packages.indices) {
            val packageInfo = packages[i]
            val tmpInfo = AppItem(
                name = packageInfo.applicationInfo.loadLabel(context.packageManager).toString(),
                packageName = packageInfo.packageName,
                icon = packageInfo.applicationInfo.loadIcon(context.packageManager)
            )
            if (sign == GetAppInfoSign.ALL) { //全手机全部应用
                appList.add(tmpInfo)
            } else if (sign == GetAppInfoSign.THIRD_PART) {
                if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM == 0) {
                    appList.add(tmpInfo) //如果非系统应用，则添加至appList
                }
            } else if (sign == GetAppInfoSign.SYSTEM) {
                if (packageInfo.applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM != 0) {
                    appList.add(tmpInfo) //如果非系统应用，则添加至appList
                }
            }

        }
        return appList
    }
}