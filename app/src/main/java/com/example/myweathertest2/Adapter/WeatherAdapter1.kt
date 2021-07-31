package com.example.myweathertest2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweathertest2.Model.ModelWeather
import com.example.myweathertest2.R

class WeatherAdapter1 (var items : Array<ModelWeather>) : RecyclerView.Adapter<WeatherAdapter1.ViewHolder>() {
    // 뷰 홀더 만들어서 반환, 뷰릐 레이아웃은 list_item_weather1.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter1.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list_weather1, parent, false)
        return ViewHolder(itemView)
    }

    // 전달받은 위치의 아이템 연결
    override fun onBindViewHolder(holder: WeatherAdapter1.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    // 아이템 갯수 리턴
    override fun getItemCount() = items.count()

    // 뷰 홀더 설정
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun setItem(item : ModelWeather) {
            val imgWeather = itemView.findViewById<ImageView>(R.id.imgWeather)  // 날씨 이미지
            val tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)           // 온도
            val tvHumidity = itemView.findViewById<TextView>(R.id.tvHumidity)   // 습도
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)           // 시각

            imgWeather.setImageResource(getWeatherImage(item.sky))
            tvTemp.text = item.temp + "°"
            tvHumidity.text = "습도 : ${item.humidity}"
            tvTime.text = item.fcstTime
        }
    }

    fun getWeatherImage(sky : String) : Int {
        // 하능 상태
        return when(sky) {
            "1" ->R.drawable.sun              // 맑음
            "3" ->  R.drawable.very_cloudy    // 구름 많음
            "4" -> R.drawable.cloudy           // 흐림
            else -> R.drawable.ic_launcher_foreground // 오류
        }
    }
}