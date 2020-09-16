package com.jinu.jjimagepicker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jinu.jjimagepicker.viewmodel.MainActivityViewModel

class JJImagePickerActivity : AppCompatActivity() {
    private val viewModel: MainActivityViewModel by viewModels()

    companion object {
        const val COUNTABLE = "COUNTABLE"
        const val MAX_SELECTABLE = "MAX_SELECTABLE"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jjimagepicker)

        setExtraSettings()
    }

    private fun setExtraSettings() {
        val countable = intent.getBooleanExtra(COUNTABLE, true)
        val maxSelectable = intent.getIntExtra(MAX_SELECTABLE, 9)

        viewModel.setExtraSettings(countable, maxSelectable)
    }
}