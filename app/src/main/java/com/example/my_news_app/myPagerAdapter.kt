package com.example.my_news_app

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter


class myPagerAdapter(val fm: FragmentManager, val listFragment: ArrayList<Fragment>): FragmentStatePagerAdapter(fm) {



    override fun getItem(position: Int): Fragment {
        return listFragment.get(position)
    }

    override fun getCount(): Int {
        return listFragment.size
    }
}