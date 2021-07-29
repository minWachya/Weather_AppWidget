package com.example.myweathertest2.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myweathertest2.Model.ModelWeather
import com.example.myweathertest2.R

class WeatherAdapter (var items : Array<ModelWeather>) : RecyclerView.Adapter<WeatherAdapter.ViewHolder>() {
    // 뷰 홀더 만들어서 반환, 뷰릐 레이아웃은 list_item_weather.xml
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherAdapter.ViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.list_item_weather, parent, false)
        return ViewHolder(itemView)
    }

    // 전달받은 위치의 아이템 연결
    override fun onBindViewHolder(holder: WeatherAdapter.ViewHolder, position: Int) {
        val item = items[position]
        holder.setItem(item)
    }

    // 아이템 갯수 리턴
    override fun getItemCount() = items.count()

    // 뷰 홀더 설정
    inner class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        fun setItem(item : ModelWeather) {
            val tvTime = itemView.findViewById<TextView>(R.id.tvTime)           // 시각
            val tvRainType = itemView.findViewById<TextView>(R.id.tvRainType)   // 강수 형태
            val tvHumidity = itemView.findViewById<TextView>(R.id.tvHumidity)   // 습도
            val tvSky = itemView.findViewById<TextView>(R.id.tvSky)             // 하늘 상태
            val tvTemp = itemView.findViewById<TextView>(R.id.tvTemp)           // 온도

            tvTime.text = item.fcstTime
            tvRainType.text = getRainType(item.rainType)
            tvHumidity.text = item.humidity
            tvSky.text = getSky(item.sky)
            tvTemp.text = item.temp + "°"
        }
    }

    // 강수 형태
    fun getRainType(rainType : String) : String {
        var result = ""
        when(rainType) {
            "0" -> result = "없음"
            "1" -> result = "비"
            "2" -> result = "비/눈"
            "3" -> result = "눈"
            else -> "오류 rainType : " + rainType
        }
        return result
    }

    // 하늘 상태
    fun getSky(sky : String) : String {
        var result = ""
        when(sky) {
            "1" -> result = "맑음"
            "3" -> result = "구름 많음"
            "4" -> result = "흐림"
            else -> "오류 rainType : " + sky
        }
        return result
    }
}