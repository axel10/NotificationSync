package com.example.notificationsync

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout

class SettingItem(context: Context?, attrs: AttributeSet) :
    ConstraintLayout(context, attrs) {
    init {
        LayoutInflater.from(context).inflate(R.layout.setting_item, this, true)
        val text = findViewById<TextView>(R.id.detail)
        val ta: TypedArray = context!!.obtainStyledAttributes(attrs, R.styleable.SettingItem)
        try {
            text.text = ta.getString(R.styleable.SettingItem_title)
        } finally {
            ta.recycle()
        }
    }

    private var listener: OnClickListener? = null

    override fun dispatchTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_UP) {
            if (listener != null) listener!!.onClick(this)
        }
        return super.dispatchTouchEvent(event)
    }

    override fun setOnClickListener(listener: OnClickListener?) {
        this.listener = listener
    }

    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event!!.action == KeyEvent.ACTION_UP && (event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER || event.keyCode == KeyEvent.KEYCODE_ENTER)) {
            if (listener != null) listener!!.onClick(this)
        }
        return super.dispatchKeyEvent(event)
    }

}