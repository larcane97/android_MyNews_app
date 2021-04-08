package com.example.my_news_app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import android.net.Uri
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.main_recycler_item_view.view.*

val dataSet= ArrayList<newsItems>()
var AllDataSet: MutableMap<String,newsItems> = mutableMapOf()
var newsNameList= ArrayList<String>()

class MainActivity : AppCompatActivity() {
    lateinit var viewPager: ViewPager
    lateinit var tabLayout: TabLayout
    lateinit var totalNewsNameList:Array<CharSequence>
    lateinit var totalNewsLogoList:ArrayList<String>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        newsNameList = getNameList(getSharedPreferences("sp", Context.MODE_PRIVATE))

        val task = myNews(newsNameList)
        task.execute()

        AllDataSet = task.get()
        for(i in newsNameList){
            AllDataSet[i]?.let {
                dataSet.add(
                    it
                )
            }
        }
        totalNewsNameList = task.news_name.eachText().toTypedArray()
        totalNewsLogoList = task.logo_list as ArrayList<String>

        InitViewPager().execute(CreateListFragment())

    }



    override fun onPause() {
        super.onPause()
        setNameList(getSharedPreferences("sp", Context.MODE_PRIVATE))
    }

    override fun onDestroy() {
        super.onDestroy()
        setNameList(getSharedPreferences("sp", Context.MODE_PRIVATE))
    }

    override fun onResume() {
        super.onResume()
        newsNameList = getNameList(getSharedPreferences("sp", Context.MODE_PRIVATE))
    }

    fun CreateListFragment(): ArrayList<Fragment> {
        val listFragments: ArrayList<Fragment> = ArrayList<Fragment>()
        listFragments.add(
            CompletedFragment(this@MainActivity)
        )
        listFragments.add(Fragment(R.layout.mynews_list))

        val addMynewsFrag = addMynews()
        addMynewsFrag.totalNewsNameList= totalNewsNameList
        addMynewsFrag.totalNewsLogoList=totalNewsLogoList
        listFragments.add(addMynewsFrag)
//        listFragments.add(Fragment(R.layout.activity_add_mynews))
        listFragments.add(Fragment(R.layout.user_config))

        return listFragments
    }

    inner class InitViewPager : AsyncTask<ArrayList<Fragment>, Any, Any>() {
        override fun doInBackground(vararg params: ArrayList<Fragment>?) {
            viewPager = findViewById(R.id.pager)

            val pageAdapter = params[0]?.let { myPagerAdapter(supportFragmentManager, it) }
            viewPager.adapter = pageAdapter

            tabLayout = findViewById(R.id.tabLayout)
            tabLayout.addTab(tabLayout.newTab().setText("마이뉴스"))
            tabLayout.addTab(tabLayout.newTab().setText("스크랩뉴스"))
            tabLayout.addTab(tabLayout.newTab().setText("추가하기"))
            tabLayout.addTab(tabLayout.newTab().setText("설정"))

            viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabReselected(p0: TabLayout.Tab?) {

                }

                override fun onTabUnselected(p0: TabLayout.Tab?) {

                }

                override fun onTabSelected(p0: TabLayout.Tab?) {
                    pager.setCurrentItem(p0?.position ?: 0)
                }
            })


        }

    }



}


class CompletedFragment(val activity: Activity) : Fragment() {
    lateinit var completedList: RecyclerView
    lateinit var newsAdapter: myNewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.main_recycler_view, container, false)
        completedList = view.findViewById(R.id.main_recycler_view)
        newsAdapter = myNewsAdapter(Glide.with(activity), dataSet, activity)

        completedList.adapter = newsAdapter

        return view
    }


}

class myNewsAdapter(
    val glide: RequestManager,
    private var dataSet: ArrayList<newsItems>,
    val activity: Activity
) :
    RecyclerView.Adapter<myNewsAdapter.ViewHolder>() {
    lateinit var vg: ViewGroup

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView
        val logo: ImageView
        val mainBtn: TextView
        val linearLayout: LinearLayout
        val block: LinearLayout

        init {
            name = view.findViewById(R.id.main_itemview_name)
            logo = view.findViewById(R.id.main_itemview_logo)
            mainBtn = view.findViewById(R.id.main_itemview_mark)
            linearLayout = view.findViewById(R.id.recycler_itemView_linearLayout)
            block = linearLayout.recycler_bloc_container

        }
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.main_recycler_item_view, viewGroup, false)
        vg = viewGroup

        return ViewHolder(view)
    }


    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        viewHolder.block.removeAllViews()

        for (idx in dataSet[position].contentList.indices) {
            val l = LayoutInflater.from(vg.context)
                .inflate(R.layout.recycler_item_bloc, vg, false)
            val item = l.findViewById<TextView>(R.id.item_content)
            val img = l.findViewById<ImageView>(R.id.item_img)

            glide.load(dataSet[position].contentList[idx].newsImg)
                .placeholder(R.drawable.img_round_border).into(img)

            item.text = dataSet[position].contentList[idx].text
            viewHolder.block.addView(l)
            item.setOnClickListener {
                val intent = Intent(activity, additional_page::class.java)
                intent.putExtra("mark", dataSet[position].contentList[idx].isMarked)
                intent.putExtra("URL", dataSet[position].contentList[idx].newsURL)
                activity.startActivity(intent)
            }
        }


        viewHolder.name.text = dataSet[position].name

        viewHolder.logo.background = ShapeDrawable(OvalShape())
        viewHolder.logo.clipToOutline = true
        glide.load(dataSet[position].logo).override(50, 50).placeholder(R.drawable.img_round_border)
            .into(viewHolder.logo)

        viewHolder.mainBtn.setOnClickListener{
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse(dataSet[position].mainURL)
            intent.data = uri
            activity.startActivity(intent)
        }

    }

    override fun getItemCount() = dataSet.size

}


