package com.jinu.jjimagepicker.widget

import android.content.Context
import android.content.Intent
import com.jinu.jjimagepicker.JJImagePickerActivity

class JJImagePickerIntent(context: Context) : Intent(context, JJImagePickerActivity::class.java) {
    fun setCountable(countable: Boolean) {
        this.putExtra(JJImagePickerActivity.COUNTABLE, countable)
    }

    fun setMaxSelectable(maxSelectable: Int) {
        this.putExtra(JJImagePickerActivity.MAX_SELECTABLE, maxSelectable)
    }
}