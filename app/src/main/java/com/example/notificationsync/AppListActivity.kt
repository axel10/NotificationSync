package com.example.notificationsync

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


class AppListActivity : AppCompatActivity() {
    companion object {
        lateinit var data: MutableList<AppItem>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_list)
        setSupportActionBar(findViewById(R.id.my_toolbar))
        val appInfo: MutableList<AppItem> =
            CommonUtils.getAppInfo(applicationContext, GetAppInfoSign.ALL)
        val viewManager = LinearLayoutManager(this)
        val viewAdapter = MyAdapter(applicationContext, appInfo)

        data = appInfo
        if (StorageUtils.isFirstBoot(applicationContext)) {
            StorageUtils.setSetting(applicationContext,SettingKey.SMTP,"smtp.qq.com")
            StorageUtils.setSetting(applicationContext,SettingKey.Port,"465")
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("开启权限")
            dialog.setMessage("重要通知需开机启动则需要开启自启动和关联启动权限。（部分手机可能需要重启后才可设置关联启动权限）")
            dialog.setNegativeButton(
                "确定"
            ) { _, _ ->
                getAppDetailSettingIntent()
            }
            dialog.create().show()
        }
        if (!CommonUtils.isNotificationListenersEnabled(applicationContext)) {
            CommonUtils.gotoNotificationAccessSetting(applicationContext)
        }

        CommonUtils.toggleNotificationListenerService(applicationContext)

        StorageUtils.firstBoot(applicationContext)

        findViewById<RecyclerView>(R.id.app_list).apply {
            setHasFixedSize(true)
            // use a linear layout manager
            layoutManager = viewManager
            // specify an viewAdapter (see also next example)
            adapter = viewAdapter
        }
    }

    /**
     * 跳转到权限设置界面
     */
    private fun getAppDetailSettingIntent() {
        val intent = Intent()
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
        intent.data = Uri.fromParts("package", packageName, null)
        startActivity(intent)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        MenuInflater(applicationContext).inflate(R.menu.my_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_change_to_system_only -> {
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(
                    CommonUtils.getAppInfo(
                        applicationContext,
                        GetAppInfoSign.SYSTEM
                    )
                )
            }
            R.id.menu_change_to_not_system_only -> {
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(
                    CommonUtils.getAppInfo(
                        applicationContext,
                        GetAppInfoSign.THIRD_PART
                    )
                )
            }
            R.id.menu_change_to_all -> {
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(
                    CommonUtils.getAppInfo(
                        applicationContext,
                        GetAppInfoSign.ALL
                    )
                )
            }
            R.id.menu_change_to_selected -> {
                val appInfo = CommonUtils.getAppInfo(applicationContext, GetAppInfoSign.ALL)
                val selectedApp = StorageUtils.getAppList(applicationContext)
                val selectedAppInfo = appInfo.filter { selectedApp.contains(it.packageName) }
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(selectedAppInfo)
            }
            R.id.menu_change_to_common -> {
                val commonApp =
                    listOf(
                        "com.android.phone",
                        "com.android.contacts",
                        "com.android.mms",
                        "com.android.dialer",
                        "com.google.android.apps.messaging"
                    )
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(
                    data.filter { commonApp.contains(it.packageName) }
                )
            }
            R.id.clear -> {
                StorageUtils.clearAllApp(applicationContext)
                val adapter = findViewById<RecyclerView>(R.id.app_list).adapter as MyAdapter
                adapter.changeDataset(
                    CommonUtils.getAppInfo(
                        applicationContext,
                        GetAppInfoSign.ALL
                    )
                )
            }
            R.id.setting -> {
                startActivity(Intent(this, SettingActivity::class.java))
            }
            R.id.detail -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("说明")
                dialog.setMessage("重要通知是一款将重要的通知转换为邮件的应用。通过设置smtp服务器和邮箱账号及授权码即可接收通知邮件。")
                dialog.setNegativeButton(
                    "确定"
                ) { _, _ ->
                }
                dialog.create().show()
            }
        }
        return true
    }
}

class MyAdapter(val context: Context, var dataset: List<AppItem>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    class MyViewHolder(val view: View) : RecyclerView.ViewHolder(view)
    fun changeDataset(newDataset: List<AppItem>) {
        this.dataset = newDataset
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.my_list_item, parent, false) as View
        val holder = MyViewHolder(view)

        holder.view.setOnClickListener {
            val position = holder.adapterPosition
            val checkBox = it.findViewById<CheckBox>(R.id.check_box)
            val pkgName = dataset[position].packageName
            if (StorageUtils.isExistApp(context, pkgName)) {
                StorageUtils.removeApp(context, pkgName)
                checkBox.isChecked = false
            } else {
                StorageUtils.addApp(context, pkgName)
                checkBox.isChecked = true
            }

        }
        // set the view's size, margins, paddings and layout parameters
        return holder
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = dataset[position]
        val img = holder.view.findViewById<ImageView>(R.id.img)
        val txt = holder.view.findViewById<TextView>(R.id.textView)
        val checkBox = holder.view.findViewById<CheckBox>(R.id.check_box)
        checkBox.isChecked = false
        img.setImageDrawable(data.icon)
        txt.text = data.name

        val appInfo = StorageUtils.getAppList(context)

        if (appInfo.find { it == data.packageName } != null) {
            holder.view.findViewById<CheckBox>(R.id.check_box).isChecked = true
        }
    }

    override fun getItemCount(): Int {
        return dataset.size
    }
}

class AppItem(val name: String, val icon: Drawable, val packageName: String)