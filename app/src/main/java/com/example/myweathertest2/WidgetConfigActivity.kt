package com.example.myweathertest2

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {
    private val ACTION_SETTING_BLACK_BTN = "APPWIDGET_SEL_BLACK"    // 글씨색 검정색으로
    private val ACTION_SETTING_WHITE_BTN = "APPWIDGET_SEL_WHITE"    // 글씨색 흰색으로


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        val rdoGroup = findViewById<RadioGroup>(R.id.rdoGroup)
        val btnSetting = findViewById<Button>(R.id.btnSetting)

        // <설정 완료> 버튼 누르면 선택된 라디오 박스 색으로 위젯 텍스트 색 변경
        btnSetting.setOnClickListener {
            val intent = Intent(this@WidgetConfigActivity, WeatherAppWidgetProvider::class.java)
            when (rdoGroup.checkedRadioButtonId) {
                // 흰색
                R.id.rdoWhite -> intent.action = ACTION_SETTING_WHITE_BTN
                // 검정색
                R.id.rdoBlack -> intent.action = ACTION_SETTING_BLACK_BTN
                // 기타
                else -> intent.action = ACTION_SETTING_BLACK_BTN
            }
            // 브로드캐스팅
            sendBroadcast(intent)
        }
    }
}