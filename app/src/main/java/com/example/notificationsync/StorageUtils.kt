package com.example.notificationsync

import android.content.Context
import android.text.TextUtils

enum class SettingKey {
    ReceiverMail,
    SenderMail,
    Password,
    SMTP,
    Port
}

object StorageUtils {
    private const val SHARED_PRE_FILE_KEY = "kotlinsharedpreference"
    private const val FIRST_BOOT_KEY = "first_boot"
    private const val APP_KEY = "app"

    fun checkSettingIsComplete(context: Context): Boolean {
        val receiverMail = getSetting(context, SettingKey.ReceiverMail)
        val senderMail = getSetting(context, SettingKey.SenderMail)
        val password = getSetting(context, SettingKey.Password)
        val smtp = getSetting(context, SettingKey.SMTP)
        val port = getSetting(context, SettingKey.Port)
        return !(TextUtils.isEmpty(receiverMail) || TextUtils.isEmpty(senderMail) || TextUtils.isEmpty(
            password
        ) || TextUtils.isEmpty(smtp) || TextUtils.isEmpty(port)
                )
    }

    fun firstBoot(context: Context) {
        setKey(context, FIRST_BOOT_KEY, "1")
    }

    fun isFirstBoot(context: Context): Boolean {
        return TextUtils.isEmpty(getKey(context, FIRST_BOOT_KEY))
    }

    private fun initAppList(context: Context) {
        val sp = context.getSharedPreferences(SHARED_PRE_FILE_KEY, Context.MODE_PRIVATE)
        val appsString = sp.getString(APP_KEY, "")
        appList = appsString!!.split(",").toMutableList()
    }

    fun getAppList(context: Context): MutableList<String> {
        if (appList == null) {
            val sp = context.getSharedPreferences(SHARED_PRE_FILE_KEY, Context.MODE_PRIVATE)
            val appsString = sp.getString(APP_KEY, "")
            appList = appsString!!.split(",").toMutableList()
            return appList as MutableList<String>
        }
        return appList as MutableList<String>
    }

    fun clearAllApp(context: Context) {
        removeKey(context, APP_KEY)
    }

    private fun setKey(context: Context, key: String, value: String) {
        val sp = context.getSharedPreferences(SHARED_PRE_FILE_KEY, Context.MODE_PRIVATE)
        with(sp.edit()) {
            putString(key, value)
            commit()
        }
    }

    private fun getKey(context: Context, key: String): String? {
        val sp = context.getSharedPreferences(SHARED_PRE_FILE_KEY, Context.MODE_PRIVATE)
        return sp.getString(key, null)
    }

    fun setSetting(context: Context, key: SettingKey, value: String) {
        setKey(context, key.toString(), value)
    }

    fun getSetting(context: Context, key: SettingKey): String? {
        return getKey(context, key.toString())
    }

    private fun removeKey(context: Context, key: String) {
        val sp = context.getSharedPreferences(SHARED_PRE_FILE_KEY, Context.MODE_PRIVATE)
        with(sp.edit()) {
            remove(key)
            commit()
        }
    }

    var appList: MutableList<String>? = null

    fun addApp(context: Context, packageName: String) {
        if (TextUtils.isEmpty(packageName)) return
        if (appList == null) {
            initAppList(context)
        }
        if (!appList!!.contains(packageName)) {
            appList!!.add(packageName)
            setKey(context, APP_KEY, appList!!.joinToString(","))
        }
    }

    fun removeApp(context: Context, packageName: String) {
        if (appList == null) {
            initAppList(context)
        }
        if (appList!!.contains(packageName)) {
            appList!!.remove(packageName)
            setKey(context, APP_KEY, appList!!.joinToString(","))
        }
    }

    fun isExistApp(context: Context, packageName: String): Boolean {
        val apps = getAppList(context)
        return apps.contains(packageName)
    }
}