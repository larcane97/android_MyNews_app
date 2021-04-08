package com.example.my_news_app

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.webkit.WebSettings
import android.webkit.WebViewClient
import android.widget.Toast
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.activity_addtional_page.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document

class additional_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addtional_page)

        val url = intent.getStringExtra("URL")
        var mark = intent.getBooleanExtra("mark",false)
        val settings:WebSettings = web_view.settings

        web_view.setWebViewClient(WebViewClient())
        settings.javaScriptEnabled=true
        settings.setSupportMultipleWindows(false)
        settings.javaScriptCanOpenWindowsAutomatically=false
        settings.useWideViewPort=false
        settings.layoutAlgorithm= WebSettings.LayoutAlgorithm.SINGLE_COLUMN

        if (!mark)
            sub_mark.setImageResource(R.drawable.ic_baseline_star_border_24)
        else
            sub_mark.setImageResource(R.drawable.ic_baseline_star_24)

        sub_mark.setOnClickListener {
            mark = !mark
            if (!mark) {
                sub_mark.setImageResource(R.drawable.ic_baseline_star_border_24)
                Toast.makeText(this,"스크랩이 취소되었습니다.",Toast.LENGTH_SHORT).show()
            }
            else {
                sub_mark.setImageResource(R.drawable.ic_baseline_star_24)
                Toast.makeText(this,"스크랩 되었습니다.",Toast.LENGTH_SHORT).show()
            }
        }



        val task = getHTML(url?:"https://news.naver.com/main/ranking/popularDay.nhn?mid=etc&sid1=111")
        task.execute()
        val doc = task.get()


        var tmp = (doc.select("div._article_body_contents").toString()).split("<a href")[0]
        val news_title = doc.select("div.article_info h3")
        tmp = tmp.replace("<img src","<img style=\"max-width: 100%; height: auto;\" src")


        if(tmp.contains("vod_area")) {
            tmp = tmp.removeRange(
                tmp.indexOf("<iframe", tmp.indexOf("vod_area")),
                tmp.indexOf("</iframe>", tmp.indexOf("vod_area")) + 9
            )
            tmp =
                tmp.substring(0..tmp.indexOf("동영상 뉴스</h4>") + 10) + "<img style=\"max-width: 100%; height: auto;\" src=https://ssl.pstatic.net/static.news/image/news/errorimage/noimage_70x70_1.png><br><br>" + tmp.substring(
                    tmp.indexOf("동영상 뉴스</h4>") + 11..tmp.length - 1
                )
        }

        sub_newsTitle.setText(news_title.text())
        val html =  "<html><br><br><br><body style=\"font-size:large;line-height:1.5em;-webkit-tap-highlight-color: rgba(0,0,0,0)\">"+tmp+"<p style=\"font-style:italic;font-weight:bold;\">"+"MY NEWS"+"</p></body></html>"
        web_view.loadDataWithBaseURL(null,html,"text/HTML","UTF-8",null)

    }



    class getHTML(val url:String):AsyncTask<Int, Int, Document>(){
        override fun doInBackground(vararg params: Int?): Document {
            val doc = Jsoup.connect(url).get()
            return doc
        }
    }
}