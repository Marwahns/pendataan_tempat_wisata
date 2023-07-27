package pnj.uas.ti.marwah_nur_shafira.fragment

import android.view.LayoutInflater
import android.view.ViewGroup
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import android.widget.AdapterView
import android.widget.ListView
import pnj.uas.ti.marwah_nur_shafira.R
import pnj.uas.ti.marwah_nur_shafira.adapter.AdapterNewsList
import pnj.uas.ti.marwah_nur_shafira.data.Article
import pnj.uas.ti.marwah_nur_shafira.data.NewsResponse
import pnj.uas.ti.marwah_nur_shafira.`interface`.NewsApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ArticleFragment : Fragment() {
    private lateinit var listView: ListView
    private lateinit var adapter: AdapterNewsList

    private val apiKey = "a7de51086ba4493ca74943973472cb45"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_article, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listView = view.findViewById(R.id.listView)
        adapter = AdapterNewsList(requireActivity(), R.layout.item_list_layout)
        listView.adapter = adapter

        val retrofit = Retrofit.Builder()
            .baseUrl("https://newsapi.org/v2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val apiService = retrofit.create(NewsApiService::class.java)

        apiService.getNews("travel, destinations, holiday", null, "publishedAt", apiKey, "en")
            .enqueue(object : Callback<NewsResponse> {
                override fun onResponse(call: Call<NewsResponse>, response: Response<NewsResponse>) {
                    if (response.isSuccessful) {
                        val newsResponse = response.body()
                        val articles = newsResponse?.articles ?: emptyList()
                        showNews(articles)
                    } else {
                        // Handle API error here
                        Log.e("API Error", "Error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<NewsResponse>, t: Throwable) {
                    // Handle failure here
                    Log.e("API Failure", "Error: ${t.message}")
                }
            })

        // Panggil fungsi hideMyLocationButton() pada listener ketika ingin menampilkan tombol btnMyLocation
        (activity as? ArticleFragment.FragmentArticleListener)?.hideMyLocationButton()

        listView.onItemClickListener = AdapterView.OnItemClickListener{ parent, view, position, id ->
            val data = parent.getItemAtPosition(position) as Article
            movePageDetail(data)
        }
    }

    private fun showNews(articles: List<Article>) {
        adapter.addAll(articles)
        adapter.notifyDataSetChanged()
    }

    fun movePageDetail(data: Article){
        val pageDetail = DetailNewsFragment()
        val bundle = Bundle()

        pageDetail.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_layout, DetailNewsFragment().newInstance(data))

        // Memberikan value untuk berpindah halaman
        // addToBackStack = agar ketika berpindah kehalaman sebelumnya, maka tidak langsung keluar dari aplikasi. Tetapi akan kembali ke fragment sebelumnya
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        Log.e("E", "move page")
    }

    interface FragmentArticleListener {
        fun hideMyLocationButton()
    }

}