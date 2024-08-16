package com.swan.timerangepicker

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.swan.timerangepicker.TimeRangePickerDialog.ConfirmAction


class MainActivity : AppCompatActivity() {
    var tvPushTime: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        tvPushTime = findViewById<View>(R.id.tvPushTime) as TextView

        findViewById<View>(R.id.setTimeLayout).setOnClickListener { view: View? ->
            val dialog =
                TimeRangePickerDialog(
                    this@MainActivity,
                    tvPushTime!!.text.toString(),
                    object : ConfirmAction {
                        override fun onLeftClick() {
                        }

                        override fun onRightClick(startAndEndTime: String) {
                            tvPushTime!!.text = startAndEndTime
                        }
                    })
            dialog.show()
        }
    }
}