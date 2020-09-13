package com.jinu.jjimagepickersample

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import com.jinu.jjimagepicker.widget.JJImagePickerIntent
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    private val startForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            println(result.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_open.setOnClickListener {
            val intent = JJImagePickerIntent(this)
            intent.setCountable(true)
            intent.setMaxSelectable(9)
            intent.setSpanCount(3)

            startForResult.launch(intent)
        }
    }
}