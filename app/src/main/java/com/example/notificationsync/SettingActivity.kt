package com.example.notificationsync

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import java.util.regex.Pattern


class SettingActivity : AppCompatActivity() {

    companion object {
        lateinit var alertBuilder: AlertDialog.Builder
        lateinit var alertTextEditor: TextView
        lateinit var alertTextView: View
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)
        context = applicationContext
        val newAlertBuilder = AlertDialog.Builder(this)
        val editTextView = layoutInflater.inflate(R.layout.dialog, null)
        alertBuilder = newAlertBuilder
        alertBuilder.setView(editTextView)
//        val editTextView = layoutInflater.inflate(R.layout.dialog, parent,false)
//        alert.setView(editTextView)
        val edittext = editTextView.findViewById<EditText>(R.id.dialog_text_edit)
        alertTextView = editTextView
        alertTextEditor = edittext
        alertBuilder.setNegativeButton(
            "取消"
        ) { dialog, whichButton ->
        }
        alertBuilder.setOnDismissListener {
            alertTextEditor.text = ""
            alertTextEditor.inputType = InputType.TYPE_CLASS_TEXT
        }
        alertBuilder.create()
        findViewById<TextView>(R.id.receive_email_text).text =
            StorageUtils.getSetting(applicationContext, SettingKey.ReceiverMail)
        findViewById<TextView>(R.id.sender_email_text).text =
            StorageUtils.getSetting(applicationContext, SettingKey.SenderMail)
        findViewById<TextView>(R.id.password_text).text =
            "".padEnd(
                StorageUtils.getSetting(applicationContext, SettingKey.SenderMail)?.length ?: 0,
                '*'
            )
        findViewById<TextView>(R.id.smtp_text).text =
            StorageUtils.getSetting(applicationContext, SettingKey.SMTP)
        findViewById<TextView>(R.id.port_text).text =
            StorageUtils.getSetting(applicationContext, SettingKey.Port)

        findViewById<LinearLayout>(R.id.receive_email).setOnClickListener {
            alertTextEditor.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            showDialog(
                SettingKey.ReceiverMail,
                "请输入接收邮箱",
                DialogInterface.OnClickListener { _, _ ->
                    val text: String = alertTextEditor.text.toString()
                    val p = Pattern.compile("^\\w+@[a-z0-9]+\\.[a-z]{2,4}\$")
                    if (!p.matcher(text).matches()) {
                        Toast.makeText(applicationContext, "请输入正确的邮箱", Toast.LENGTH_SHORT).show()
                    } else {
                        StorageUtils.setSetting(applicationContext, SettingKey.ReceiverMail, text)
                        findViewById<TextView>(R.id.receive_email_text).text = text
                    }
                })
        }

        findViewById<LinearLayout>(R.id.sender_email).setOnClickListener {
            alertTextEditor.inputType = InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
            showDialog(
                SettingKey.SenderMail,
                "请输入发送邮箱",
                DialogInterface.OnClickListener { _, _ ->
                    val text: String = alertTextEditor.text.toString()
                    val p = Pattern.compile("^\\w+@[a-z0-9]+\\.[a-z]{2,4}\$")
                    if (!p.matcher(text).matches()) {
                        Toast.makeText(applicationContext, "请输入正确的邮箱", Toast.LENGTH_SHORT).show()
                    } else {
                        StorageUtils.setSetting(applicationContext, SettingKey.SenderMail, text)
                        findViewById<TextView>(R.id.sender_email_text).text = text
                    }
                })
        }

        findViewById<LinearLayout>(R.id.password).setOnClickListener {
            alertTextEditor.inputType = InputType.TYPE_TEXT_VARIATION_PASSWORD
            showDialog(
                SettingKey.Password,
                "请输入授权码",
                DialogInterface.OnClickListener { _, _ ->
                    val text: String = alertTextEditor.text.toString()
                    StorageUtils.setSetting(applicationContext, SettingKey.Password, text)
                    findViewById<TextView>(R.id.password_text).text = "".padEnd(text.length, '*')
                })
        }

        findViewById<LinearLayout>(R.id.smtp).setOnClickListener {
            showDialog(
                SettingKey.SMTP,
                "请输入SMTP服务器地址",
                DialogInterface.OnClickListener { _, _ ->
                    val text: String = alertTextEditor.text.toString()
                    val p = Pattern.compile("^(([A-Za-z0-9-~]+)\\.)+([A-Za-z0-9-~/])+\$")
                    if (!p.matcher(text).matches()) {
                        Toast.makeText(applicationContext, "请输入正确的SMTP服务器地址", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        StorageUtils.setSetting(applicationContext, SettingKey.SMTP, text)
                        findViewById<TextView>(R.id.smtp_text).text = text
                    }
                })
        }

        findViewById<LinearLayout>(R.id.port).setOnClickListener {
            alertTextEditor.inputType = InputType.TYPE_CLASS_NUMBER
            showDialog(
                SettingKey.Port,
                "请输入端口",
                DialogInterface.OnClickListener { _, _ ->
                    val text: String = alertTextEditor.text.toString()
                    StorageUtils.setSetting(applicationContext, SettingKey.Port, text)
                    findViewById<TextView>(R.id.port_text).text = text
                })
        }
    }

/*    fun addSettingClickListener(
        context: Context,
        id: Int,
        settingKey: SettingKey,
        listener: DialogInterface.OnClickListener,
        type: Int,
        checker: () -> Boolean,
        title: String
    ) {
        alertTextEditor.inputType = type
        showDialog(
            title,
            listener
        )
    }*/

    // 负责显示对话框和保存数据
    private fun showDialog(
        key: SettingKey,
        title: String,
        listener: DialogInterface.OnClickListener
    ) {
        if (alertTextView.parent != null) {
            (alertTextView.parent as ViewGroup).removeView(alertTextView)
        }
        alertBuilder.setTitle(title)
        alertBuilder.setPositiveButton(
            "确定"
            , listener
        )
        if (key != SettingKey.Password) alertTextEditor.text = StorageUtils.getSetting(context, key)
        alertBuilder.show()
    }
}

