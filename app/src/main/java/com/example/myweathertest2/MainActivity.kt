package com.example.myweathertest2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

// 메인 액티비티
class MainActivity : AppCompatActivity() {
    lateinit var tvBaseTime : TextView      // baseTime
    lateinit var tvFcstTime : TextView      // fcstTime
    lateinit var tvFcstDate : TextView      // fcstDate

    lateinit var tvRainRatio : TextView     // 강수 확률
    lateinit var tvRainType : TextView      // 강수 형태
    lateinit var tvHumidity : TextView      // 습도
    lateinit var tvSky : TextView           // 하늘 상태
    lateinit var tvTemp : TextView          // 온도
    lateinit var tvRecommends : TextView    // 옷 추천
    lateinit var btnRefresh : Button        // 새로고침 버튼

    var base_date = "20210510"  // 발표 일자
    var base_time = "1400"      // 발표 시각
    var nx = "55"               // 예보지점 X 좌표
    var ny = "127"              // 예보지점 Y 좌표

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvBaseTime = findViewById(R.id.tvBaseTime)
        tvFcstTime = findViewById(R.id.tvFcstTime)
        tvFcstDate = findViewById(R.id.tvFcstDate)

        tvRainRatio = findViewById(R.id.tvRainRatio)
        tvRainType = findViewById(R.id.tvRainType)
        tvHumidity = findViewById(R.id.tvHumidity)
        tvSky = findViewById(R.id.tvSky)
        tvTemp = findViewById(R.id.tvTemp)
        tvRecommends = findViewById(R.id.tvRecommends)
        btnRefresh = findViewById(R.id.btnRefresh)

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
        val time = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시간
        // API 가져오기 적당하게 변환
        base_time = getTime(time)
        // 현재 시각이 00시 01시라면 어제 예보한 데이터 가져오기
        if (time == "00" || time == "01") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

        // 날씨 정보 가져오기
        // (응답 자료 형식-"JSON", 한 페이지 결과 수 = 10, 페이지 번호 = 1, 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather(10, 1, "JSON", base_date, base_time, nx, ny)

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    var it: List<ITEM> = response.body()!!.response.body.items.item

                    var rainRatio = ""      // 강수 확률
                    var rainType = ""       // 강수 형태
                    var humidity = ""       // 습도
                    var sky = ""            // 하능 상태
                    var temp = ""           // 기온
                    for (i in 0..9) {
                        when(it[i].category) {
                            "POP" -> rainRatio = it[i].fcstValue    // 강수 기온
                            "PTY" -> rainType = it[i].fcstValue     // 강수 형태
                            "REH" -> humidity = it[i].fcstValue     // 습도
                            "SKY" -> sky = it[i].fcstValue          // 하늘 상태
                            "TMP" -> temp = it[i].fcstValue         // 기온
                            else -> continue
                        }

                    }
                    // 날씨 정보 텍스트뷰에 보이게 하기
                    setTextView(rainRatio, rainType, humidity, sky, temp)
                    tvBaseTime.text = "baseTime : " + base_time
                    tvFcstTime.text = "fcstTime : " + it[0].fcstTime
                    tvFcstDate.text = "fcstDate : " + it[0].fcstDate

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

    // 텍스트 뷰에 날씨 정보 보여주기
    fun setTextView(rainRatio : String, rainType : String, humidity : String, sky : String, temp : String) {
        // 강수 확률
        tvRainRatio.text = rainRatio + "%"
        // 강수 형태
        var result = ""
        when(rainType) {
            "0" -> result = "없음"
            "1" -> result = "비"
            "2" -> result = "비/눈"
            "3" -> result = "눈"
            else -> "오류 rainType : " + rainType
        }
        tvRainType.text = result
        // 습도
        tvHumidity.text = humidity + "%"
        // 하능 상태
        when(sky) {
            "1" -> result = "맑음"
            "3" -> result = "구름 많음"
            "4" -> result = "흐림"
            else -> "오류"
        }
        tvSky.text = result
        // 온도
        tvTemp.text = temp + "°"
        // 기본 옷 추천
        when (temp.toInt()) {
            in 5..8 -> result = "울 코트, 가죽 옷, 기모"
            in 9..11 -> result = "트렌치 코트, 야상, 점퍼"
            in 12..16 -> result = "자켓, 가디건, 청자켓"
            in 17..19 -> result = "니트, 맨투맨, 후드, 긴바지"
            in 20..22 -> result = "블라우스, 긴팔 티, 슬랙스"
            in 23..27 -> result = "얇은 셔츠, 반바지, 면바지"
            in 28..50 -> result = "민소매, 반바지, 린넨 옷"
            else -> result = "패딩, 누빔 옷, 목도리"
        }
        tvRecommends.text = result
    }

    // baseTime 설정하기
    fun getTime(time : String) : String {
        var baseTime = ""
        when(time) {                // baseTime   // 현재 시간대     // fcstTime
            in "02".."04" -> baseTime = "0200"    // 02~04          // 0300
            in "05".."07" -> baseTime = "0500"    // 05~07          // 0600
            in "08".."10" -> baseTime = "0800"    // 08~10          // 0900
            in "11".."13" -> baseTime = "1100"    // 11~13          // 1200
            in "14".."16" -> baseTime = "1400"    // 12~16          // 1500
            in "17".."19" -> baseTime = "1700"    // 17~19          // 1700
            in "20".."22" -> baseTime = "2000"    // 20~22          // 2000
            else -> baseTime = "2000"             // 23~01          // 0000
        }
        return baseTime
    }


}