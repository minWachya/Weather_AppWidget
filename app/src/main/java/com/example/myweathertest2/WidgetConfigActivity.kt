package com.example.myweathertest2

import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        val rdoGroup = findViewById<RadioGroup>(R.id.rdoGroup)
        val btnSetting = findViewById<Button>(R.id.btnSetting)

        // <설정 완료> 버튼 누르면 선택된 라디오 박스 색으로 위젯 텍스트 색 변경
        btnSetting.setOnClickListener {
            var color = Color.BLACK
            when (rdoGroup.checkedRadioButtonId) {
                // 흰색
                R.id.rdoWhite -> color = Color.WHITE
                R.id.rdoBlack -> color = Color.BLACK
            }
        }
    }
}