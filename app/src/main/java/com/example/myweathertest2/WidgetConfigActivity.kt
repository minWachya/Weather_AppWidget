package com.example.myweathertest2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity

class WidgetConfigActivity : AppCompatActivity() {
    private val WIDGET_SETTING_BLACK = "@string/WIDGET_SETTING_BLACK"   // 글씨색 검정색으로 설정
    private val WIDGET_SETTING_WHITE = "@string/WIDGET_SETTING_WHITE"   // 글씨색 흰색으로 설정

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_widget_config)

        val rdoGroup = findViewById<RadioGroup>(R.id.rdoGroup)
        val btnSetting = findViewById<Button>(R.id.btnSetting)
        val rdoBlack = findViewById<RadioButton>(R.id.rdoBlack)
        val rdoWhite = findViewById<RadioButton>(R.id.rdoWhite)

        // 이전에 설정했던 색의 라디오 버튼 선택되게(기본은 검정색)
        val prevColor = getSharedPreferences("pref", MODE_PRIVATE).getString("SELECT_COLOR", "black")
        if (prevColor == "white") rdoWhite.isChecked = true
        else rdoBlack.isChecked = true

        // <설정 완료> 버튼 누르면 선택된 색으로 위젯 TextView 색 변경
        btnSetting.setOnClickListener {
            val intent = Intent(this@WidgetConfigActivity, WeatherAppWidgetProvider::class.java)
            val pref = getSharedPreferences("pref", MODE_PRIVATE)
            val edit = pref.edit()
            when (rdoGroup.checkedRadioButtonId) {
                // 흰색
                R.id.rdoWhite -> {
                    intent.action = WIDGET_SETTING_WHITE
                    edit.putString("SELECT_COLOR", "white")
                }
                // 검정색
                R.id.rdoBlack -> {
                    intent.action = WIDGET_SETTING_BLACK
                    edit.putString("SELECT_COLOR", "black")
                }
                // 기타
                else -> intent.action = WIDGET_SETTING_BLACK
            }
            edit.apply()

            // 브로드캐스팅(위젯으로 정보 보내기)
            sendBroadcast(intent)

            finish()
        }
    }
}