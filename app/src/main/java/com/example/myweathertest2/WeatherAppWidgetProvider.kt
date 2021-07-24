package com.example.myweathertest2

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews

class WeatherAppWidgetProvider : AppWidgetProvider() {

//    override fun onUpdate(
//            context: Context,
//            appWidgetManager: AppWidgetManager,
//            appWidgetIds: IntArray
//    ) {
//        // Perform this loop procedure for each App Widget that belongs to this provider
//        appWidgetIds.forEach { appWidgetId ->
//            // Create an Intent to launch ExampleActivity
//            val pendingIntent: PendingIntent = Intent(context, WeatherActivity::class.java)
//                    .let { intent ->
//                        PendingIntent.getActivity(context, 0, intent, 0)
//                    }
//
//            // Get the layout for the App Widget and attach an on-click listener
//            // to the button
//            val views: RemoteViews = RemoteViews(
//                    context.packageName,
//                    R.layout.appwidget_provider_layout
//            ).apply {
//                setOnClickPendingIntent(R.id.button, pendingIntent)
//            }
//
//            // Tell the AppWidgetManager to perform an update on the current app widget
//            appWidgetManager.updateAppWidget(appWidgetId, views)
//        }
//    }

    override fun onReceive(context: Context?, intent: Intent?) {

    }


    // 유저가 앱 위젯을 최초로 추가되는 순간
    // 알람 등록
    override fun onEnabled(context: Context?) {
        super.onEnabled(context)


    }

    // 유저가 엡 위젯을 회초로 삭제하는 순간
    // 알람 해제
    override fun onDisabled(context: Context?) {
        super.onDisabled(context)
    }

}