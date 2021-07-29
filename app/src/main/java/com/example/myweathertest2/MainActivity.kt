package com.example.myweathertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myweathertest2.Adapter.WeatherAdapter
import com.example.myweathertest2.Model.ModelWeather
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

// 메인 액티비티
class MainActivity : AppCompatActivity() {
    lateinit var tvDate : TextView                  // 오늘 날짜 텍스트뷰
    lateinit var weatherRecyclerView : RecyclerView // 날씨 리사이클러 뷰
    lateinit var btnRefresh : Button        // 새로고침 버튼

    var base_date = "20210510"  // 발표 일자
    var base_time = "1400"      // 발표 시각
    var nx = "55"               // 예보지점 X 좌표
    var ny = "127"              // 예보지점 Y 좌표

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvDate = findViewById(R.id.tvDate)
        weatherRecyclerView = findViewById(R.id.weatherRecyclerView)
        btnRefresh = findViewById(R.id.btnRefresh)

        // 리사이클러 뷰 매니저 설정
        weatherRecyclerView.layoutManager = LinearLayoutManager(this@MainActivity)

        tvDate.text = SimpleDateFormat("MM월 dd일", Locale.getDefault()).format(Calendar.getInstance().time) + "날씨"

        // nx, ny지점의 날씨 가져와서 설정하기
        setWeather(nx, ny)

        // <새로고침> 버튼 누를 때 날씨 정보 다시 가져오기
        btnRefresh.setOnClickListener {
            setWeather(nx, ny)
        }
    }

    // 날씨 가져와서 설정하기
    fun setWeather(nx : String, ny : String) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val time_H = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시각
        val time_M = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 분
        // API 가져오기 적당하게 변환
        base_time = getTime(time_H, time_M)
//        // 현재 시각이 00시 01시라면 어제 예보한 데이터 가져오기
//        if (time == "00" || time == "01") {
//            cal.add(Calendar.DATE, -1).toString()
//            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
//        }

        // 날씨 정보 가져오기
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, nx, ny)

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    var it: List<ITEM> = response.body()!!.response.body.items.item

                    // 현재 시각부터 1시간 뒤의 날씨 6개를 담을 배열
                    val weatherArr = arrayOf(ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather(), ModelWeather())

                    // 배열 채우기
                    var index = 0
                    for (i in 0..59) {
                        index %= 6
                        when(it[i].category) {
                            "PTY" -> weatherArr[index].rainType = it[i].fcstValue     // 강수 형태
                            "REH" -> weatherArr[index].humidity = it[i].fcstValue     // 습도
                            "SKY" -> weatherArr[index].sky = it[i].fcstValue          // 하늘 상태
                            "T1H" -> weatherArr[index].temp = it[i].fcstValue         // 기온
                            else -> continue
                        }
                        index++
                    }

                    // 각 날짜 배열 시간 설정
                    for (i in 0..5) {
                        weatherArr[i].fcstTime = it[i].fcstTime
                    }

                    // 리사이클러 뷰에 데이터 연결
                    weatherRecyclerView.adapter = WeatherAdapter(weatherArr)

                    // 토스트 띄우기
                    Toast.makeText(applicationContext, it[0].fcstDate + ", " + it[0].fcstTime + "의 날씨 정보입니다.", Toast.LENGTH_SHORT).show()
                }
            }

            // 응답 실패 시
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })
    }

    // baseTime 설정하기
    fun getTime(h : String, m : String) : String {
        var result = ""

        if (m.toInt() < 45) {
            if (h == "00") result = "2330"
            else {
                var result_h = h.toInt() - 1
                if (result_h < 10) {
                    result = "0" + result_h + "30"
                }
                else result = result_h.toString() + "30"
            }
        }
        else result = h + "30"

        Log.d("mmm baseTime", result)
        return result
    }


}