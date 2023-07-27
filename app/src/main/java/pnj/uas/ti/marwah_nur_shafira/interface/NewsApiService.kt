package pnj.uas.ti.marwah_nur_shafira.`interface`

import pnj.uas.ti.marwah_nur_shafira.data.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {
    @GET("everything")
    fun getNews(
        @Query("q") query: String,
        @Query("from") fromDate: String?,
        @Query("sortBy") sortBy: String,
        @Query("apiKey") apiKey: String,
        @Query("language") language: String
    ): Call<NewsResponse>
}