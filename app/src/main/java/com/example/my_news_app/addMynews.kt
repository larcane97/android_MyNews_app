package com.example.my_news_app

import android.content.DialogInterface
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import kotlinx.android.synthetic.main.activity_add_mynews.*

class addMynews : Fragment() {
    lateinit var totalNewsNameList:Array<CharSequence>
    lateinit var totalNewsLogoList:ArrayList<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view:LinearLayout = inflater.inflate(R.layout.activity_add_mynews,container,false) as LinearLayout
        val glide = Glide.with(this)
        val btn= view.findViewById<Button>(R.id.add_newsbtn)



        btn.setOnClickListener {
            if (container != null) {
                AlertDialog.Builder(container.context,android.R.style.Theme_DeviceDefault_Light_Dialog_Alert)
                    .setTitle("구독할 뉴스를 선택하세요")
                    .setItems(totalNewsNameList,DialogInterface.OnClickListener { dialog, which ->
                        Toast.makeText(context,""+totalNewsNameList[which]+"를 구독하셨습니다.",Toast.LENGTH_SHORT).show()

                        newsNameList.add(totalNewsNameList[which] as String)
                        createNewItem(totalNewsNameList[which] as String,container, glide, view)
                        mainActivityRenew(totalNewsNameList[which] as String,true)

                    }).show()
            }

        }

        val iter:Iterator<String> = newsNameList.iterator()
        while(iter.hasNext()){
            val name = iter.next()
            if(name=="없음")
                continue
            createNewItem(name, container, glide, view)
        }

        return view
    }

    fun createNewItem(name:String,container: ViewGroup?,glide:RequestManager,view:LinearLayout){
        if(newsNameList[0]=="없음")
            newsNameList.remove("없음")
        val logo = totalNewsLogoList[totalNewsNameList.indexOf(name)]
        val l = LayoutInflater.from(container?.context)
            .inflate(R.layout.add_mynews_itembloc, container, false)
        val item = l.findViewById<TextView>(R.id.add_newsName)
        val img = l.findViewById<ImageView>(R.id.add_newsImg)
        val delBtn = l.findViewById<Button>(R.id.add_newsDelBtn)

        img.background = ShapeDrawable(OvalShape())
        img.clipToOutline = true
        glide.load(logo)
            .placeholder(R.drawable.img_round_border).into(img)

        item.text = name

        delBtn.setOnClickListener {
            view.removeView(l)
            //dataSet.remove(name)
            newsNameList.remove(name)
            mainActivityRenew(name,false)
            // main activity 이름목록 동기화
        }
        view.addView(l)
    }


    fun mainActivityRenew(name:String,chk:Boolean){
        if(chk) AllDataSet[name]?.let { dataSet.add(it) }
        else AllDataSet[name]?.let { dataSet.remove(it) }
    }
}