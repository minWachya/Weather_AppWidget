package com.example.myweathertest2

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

// 결과 xml 파일에 접근해서 정보 가져오기
interface WeatherInterface {
    // getVilageFcst : 단기 예보 조회 + 인증키
    @GET("getVilageFcst?serviceKey=edSnzhmFwkaoSFwGnzfI%2FVoqtQcqDM67Uzv%2BQmbp7OkjHCY6j%2B9Pq%2BriPr7jQXagfQA0GRllEZL%2BhWBQSljPIw%3D%3D")

    fun GetWeather(@Query("numOfRows") num_of_rows : Int,   // 한 페이지 경과 수
                   @Query("pageNo") page_no : Int,          // 페이지 번호
                   @Query("dataType") data_type : String,   // 응답 자료 형식
                   @Query("base_date") base_date : String,  // 발표 일자
                   @Query("base_time") base_time : String,  // 발표 시각
                   @Query("nx") nx : String,                // 예보지점 X 좌표
                   @Query("ny") ny : String)                // 예보지점 Y 좌표
            : Call<WEATHER>
}