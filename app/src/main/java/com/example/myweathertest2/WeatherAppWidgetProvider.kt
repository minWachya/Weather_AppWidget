package com.example.myweathertest2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.autofill.OnClickAction
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import android.widget.Toast
import androidx.annotation.RequiresApi
import retrofit2.Call
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeatherAppWidgetProvider : AppWidgetProvider() {

    // 위젯이 추가될 때마다 호출
    override fun onUpdate( context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray ) {
        // 앱 위젯 레이아웃 가져오기
        val views = RemoteViews( context.packageName, R.layout.weather_appwidget)

        // 해당 위젯을 클릭했을 때 특정 페이지로 이동시켜준다.
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
        views.setOnClickPendingIntent(R.id.imgSky, pendingIntent)

        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        var base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        var time = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시간
        // API 가져오기 적당하게 변환
        val base_time = getTime(time)

        // 시간 설정
        views.setTextViewText(R.id.tvTime, base_time)

        // 날씨 정보 가져오기
        // (응답 자료 형식-"JSON", 한 페이지 결과 수 = 10, 페이지 번호 = 1, 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather(10, 1, "JSON", base_date, base_time, "55", "127")

        // 비동기적으로 실행하기
        call.enqueue(object : retrofit2.Callback<WEATHER> {
            // 응답 성공 시
            @RequiresApi(Build.VERSION_CODES.M)
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    var it: List<ITEM> = response.body()!!.response.body.items.item

                    var sky = ""            // 하능 상태
                    var temp = ""           // 기온
                    for (i in 0..9) {
                        when(it[i].category) {
                            "SKY" -> sky = it[i].fcstValue          // 하늘 상태
                            "TMP" -> temp = it[i].fcstValue         // 기온
                            else -> continue
                        }
                    }
                    // 텍스트뷰 설정
                    setTextView(views, sky, temp)   // @RequiresApi(Build.VERSION_CODES.M)
                }

                // 업데이트 수행
                appWidgetManager.updateAppWidget(appWidgetIds, views)
            }

            // 응답 실패 시
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })

        // 업데이트 시간을 현재 시간으로 수정하기
        views.setTextViewText(R.id.tvUpdate, SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Calendar.getInstance().time).toString())

        // 업데이트 버튼 누르면 동작하기


        // 업데이트 수행
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }
    // 텍스트 뷰에 날씨 정보 보여주기
    @RequiresApi(Build.VERSION_CODES.M)
    fun setTextView(views : RemoteViews, sky : String, temp : String) {
        // 하능 상태
        when(sky) { // @RequiresApi(Build.VERSION_CODES.M)
            "1" -> views.setImageViewResource(R.id.imgSky, R.drawable.sun)          // 맑음
            "3" -> views.setImageViewResource(R.id.imgSky, R.drawable.very_cloudy)  // 구름 많음
            "4" -> views.setImageViewResource(R.id.imgSky, R.drawable.cloudy)       // 흐림
            else -> views.setImageViewResource(R.id.imgSky, R.drawable.ic_launcher_foreground) // 오류
        }

        // 온도
        views.setTextViewText(R.id.tvTemp, temp + "°")

        // 기본 옷 추천
        var result = ""
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
        views.setTextViewText(R.id.tvRecommends, result)
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

    // 유저가 앱 위젯을 최초로 추가될 때 호출
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    // 유저가 엡 위젯을 회초로 삭제될 때 호출
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

}