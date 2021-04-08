package com.example.my_news_app

import android.os.AsyncTask
import android.util.Log
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.io.Serializable


class newsItems(
    val logo: String,
    val name: String,
    val contentList: ArrayList<content>
    , val mainURL:String
) : Serializable

class content(
    val text: String,
    val newsURL: String,
    val newsImg: String,
    var isMarked: Boolean = false
)


class myNews(
    val nameArray: ArrayList<String>,
    val url: String = "https://news.naver.com/main/ranking/popularDay.nhn?mid=etc&sid1=111"

) : AsyncTask<Int, Int, MutableMap<String, newsItems>>() {
    lateinit var doc: Document
    lateinit var news_logo: Elements
    lateinit var logo_list: List<String>
    lateinit var news_name: Elements
    lateinit var news_contents: Elements
    lateinit var mainURL:List<String>
    private val newsItems = ArrayList<newsItems>()

    val AllDataSet:MutableMap<String,newsItems> = mutableMapOf()


    override fun doInBackground(vararg params: Int?): MutableMap<String, newsItems> {
        doc = Jsoup.connect(url).get()
        val news_rankingbox = doc.getElementsByClass("rankingnews_box")
        val test = news_rankingbox.select("strong.rankingnews_name")

        news_logo = news_rankingbox.select("span.rankingnews_thumb")
        news_name = news_rankingbox.select("strong.rankingnews_name")
        news_contents = news_rankingbox.select("ul.rankingnews_list")
        logo_list = news_logo.select("img").eachAttr("src") +
                news_logo.select("img").eachAttr("data-src")
        mainURL = news_rankingbox.select("a.rankingnews_box_head").eachAttr("href")

        Log.d("test",mainURL.size.toString())
        Log.d("test",mainURL.toString())

        for (idx in 0 until news_name.size) {
            var contents = ArrayList<content>()

            val ee = news_contents[idx].getElementsByTag("li")

            val content = ee.select("div.list_content")
            val newsURL = content.select("a").eachAttr("href")
            var newsIMG: List<String> =
                ee.select("a img").eachAttr("src") + ee.select("a img").eachAttr("data-src")
            val newsIMG1 = ArrayList<String>()
            for (i in 0 until 5) {
                newsIMG1.add("https://ssl.pstatic.net/static.news/image/news/errorimage/noimage_70x70_1.png")
            }
            for (i in newsIMG.indices) {
                newsIMG1[i] = newsIMG[i]
            }


            for (i in 0 until content.size)
                contents.add(
                    content(
                        content[i].text(),
                        "https://news.naver.com" + newsURL[i],
                        newsIMG1[i]
                    )
                )

            AllDataSet[news_name[idx].text()] = newsItems(
                logo_list[idx],
                news_name[idx].text(),
                contents,
                "https://news.naver.com"+mainURL[idx]

            )

        }

        return AllDataSet
    }

}