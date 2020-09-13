package com.jinu.jjimagepicker

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.jinu.jjimagepicker.viewmodel.MainActivityViewModel

class JJImagePickerActivity : AppCompatActivity() {
    val viewModel: MainActivityViewModel by viewModels()

    companion object {
        const val COUNTABLE = "COUNTABLE"
        const val MAX_SELECTABLE = "MAX_SELECTABLE"
        const val SPAN_COUNT = "SPAN_COUNT"
    }

    var countable: Boolean = false
    var maxSelectable: Int = 0
    var spanCount: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_jjimagepicker)

        getExtraSettings()
    }

    private fun getExtraSettings() {
        countable = intent.getBooleanExtra(COUNTABLE, true)
        maxSelectable = intent.getIntExtra(MAX_SELECTABLE, 9)
        spanCount = intent.getIntExtra(SPAN_COUNT, 3)
    }
}