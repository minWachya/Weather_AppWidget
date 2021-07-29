package com.example.myweathertest2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.Toast
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class WeatherAppWidgetProvider : AppWidgetProvider() {
    private val ACTION_BTN = "ButtonClick"

    // 위젯이 추가될 때마다 호출
    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // 앱 위젯 레이아웃 가져오기
        val views = RemoteViews(context.packageName, R.layout.weather_appwidget)

        // 날씨 이미지 클릭 시 앱으로 이동
        val pendingIntent: PendingIntent = Intent(context, MainActivity::class.java)
                .let { intent ->
                    PendingIntent.getActivity(context, 0, intent, 0)
                }
        views.setOnClickPendingIntent(R.id.imgSky, pendingIntent)

        // 업데이트 이미지 누르면 업데이트 하기
        val widgetIntent = Intent(context, WeatherAppWidgetProvider::class.java).setAction(ACTION_BTN)
        views.setOnClickPendingIntent(R.id.imgRefresh, PendingIntent.getBroadcast(context, 0, widgetIntent, 0))

        // 날씨 정보 가져오기
        getWeatherInfo(views, appWidgetManager, appWidgetIds)

        // 업데이트 수행
        appWidgetManager.updateAppWidget(appWidgetIds, views)
    }

    // 날씨 정보 가져오기
    fun getWeatherInfo(views: RemoteViews, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        // 준비 단계 : base_date(발표 일자), base_time(발표 시각)
        // 현재 날짜, 시간 정보 가져오기
        val cal = Calendar.getInstance()
        var base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time) // 현재 날짜
        val timeH = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 시각
        val timeM = SimpleDateFormat("HH", Locale.getDefault()).format(cal.time) // 현재 분
        // API 가져오기 적당하게 변환
        var base_time = getBaseTime(timeH, timeM)
        // 현재 시각이 00시이고 45분 이하여서 baseTime이 2330이면 어제 정보 받아오기
        if (timeH == "00" && base_time == "2330") {
            cal.add(Calendar.DATE, -1).toString()
            base_date = SimpleDateFormat("yyyyMMdd", Locale.getDefault()).format(cal.time)
        }

        // 시간 설정
        views.setTextViewText(R.id.tvTime, base_time)

        // 날씨 정보 가져오기
        // (한 페이지 결과 수 = 60, 페이지 번호 = 1, 응답 자료 형식-"JSON", 발표 날싸, 발표 시각, 예보지점 좌표)
        val call = ApiObject.retrofitService.GetWeather(60, 1, "JSON", base_date, base_time, "55", "127")

        // 비동기적으로 실행하기
        call.enqueue(object : Callback<WEATHER> {
            // 응답 성공 시
            override fun onResponse(call: Call<WEATHER>, response: Response<WEATHER>) {
                if (response.isSuccessful) {
                    // 날씨 정보 가져오기
                    val it: List<ITEM> = response.body()!!.response.body.items.item

                    var sky = ""            // 하능 상태
                    var temp = "-1"           // 기온
                    val totalCount = response.body()!!.response.body.totalCount - 1
                    val curTime = it[0].fcstTime    // 현재 시간대
                    for (i in 0..totalCount) {
                        // 현재 시각 정보만 가져오기
                        if (it[i].fcstTime == curTime) {
                            when (it[i].category) {
                                "SKY" -> sky = it[i].fcstValue          // 하늘 상태
                                "T1H" -> temp = it[i].fcstValue         // 기온
                                else -> continue
                            }
                        }
                        // 현재 시각 아니면 넘어가기
                        else continue
                    }
                    // 텍스트뷰 설정
                    setTextView(views, sky, temp.toInt())   // @RequiresApi(Build.VERSION_CODES.M)
                }

                // 업데이트 수행
                appWidgetManager.updateAppWidget(appWidgetIds, views)
            }

            // 응답 실패 시
            override fun onFailure(call: Call<WEATHER>, t: Throwable) {
                Log.d("api fail", t.message.toString())
            }
        })
    }


    // 텍스트 뷰에 날씨 정보 보여주기
    fun setTextView(views: RemoteViews, sky: String, temp: Int) {
        // 하능 상태
        when(sky) {
            "1" -> views.setImageViewResource(R.id.imgSky, R.drawable.sun)          // 맑음
            "3" -> views.setImageViewResource(R.id.imgSky, R.drawable.very_cloudy)  // 구름 많음
            "4" -> views.setImageViewResource(R.id.imgSky, R.drawable.cloudy)       // 흐림
            else -> views.setImageViewResource(R.id.imgSky, R.drawable.ic_launcher_foreground) // 오류
        }

        // 온도
        views.setTextViewText(R.id.tvTemp, "${temp}°")

        // 기본 옷 추천
        var result = ""
        if (temp == -1) result = "오류"
        else result = when (temp) {
            in 5..8 -> "울 코트, 가죽 옷, 기모"
            in 9..11 -> "트렌치 코트, 야상, 점퍼"
            in 12..16 -> "자켓, 가디건, 청자켓"
            in 17..19 -> "니트, 맨투맨, 후드, 긴바지"
            in 20..22 -> "블라우스, 긴팔 티, 슬랙스"
            in 23..27 -> "얇은 셔츠, 반바지, 면바지"
            in 28..50 -> "민소매, 반바지, 린넨 옷"
            else -> "패딩, 누빔 옷, 목도리"
        }
        views.setTextViewText(R.id.tvRecommends, result)

        // 업데이트 시간을 현재 시간으로 수정하기
        views.setTextViewText(R.id.tvUpdate, SimpleDateFormat("MM/dd HH:mm", Locale.getDefault()).format(Calendar.getInstance().time).toString())
    }

    // baseTime 설정하기
    private fun getBaseTime(h : String, m : String) : String {
        var result = ""

        // 45분 전이면
        if (m.toInt() < 45) {
            // 0시면 2330
            if (h == "00") result = "2330"
            // 아니면 1시간 전 날씨 정보 부르기
            else {
                var resultH = h.toInt() - 1
                // 1자리면 0 붙여서 2자리로 만들기
                if (resultH < 10) result = "0" + resultH + "30"
                // 2자리면 그대로
                else result = resultH.toString() + "30"
            }
        }
        // 45분 이후면 바로 정보 받아오기
        else result = h + "30"

        return result
    }

    // 유저가 앱 위젯을 최초로 추가될 때 호출
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)
    }

    // 유저가 엡 위젯을 회초로 삭제될 때 호출
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

    // 앱 위젯 브로드캐스트 수신
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        if (intent?.action == ACTION_BTN) {
            // 앱 위젯 레이아웃 가져오기
            val views = RemoteViews(context!!.packageName, R.layout.weather_appwidget)
            // appWidgetManager 가져오기
            val appWidgetManager = AppWidgetManager.getInstance(context)
            // 위젯 컴포넌트 가져오기
            val widget = ComponentName(context, WeatherAppWidgetProvider::class.java)
            // 날씨 정보 가져오기
            getWeatherInfo(views, appWidgetManager, appWidgetManager.getAppWidgetIds(widget))
            // 업데이트 하기
            appWidgetManager.updateAppWidget(widget, views)

            Toast.makeText(context, "업데이트 했습니다.", Toast.LENGTH_SHORT).show()
        }
    }

}