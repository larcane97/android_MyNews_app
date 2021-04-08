package com.example.my_news_app

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

fun getNameList(sp:SharedPreferences): ArrayList<String> {
    val jsonNameList=sp.getString("nameList","")
    val nameList = (Gson().fromJson(jsonNameList, object: TypeToken<ArrayList<String>>() {}.type))?: arrayListOf("없음")
    return nameList
}

fun setNameList(sp:SharedPreferences){
    val t = sp.edit()
    t.apply {
        val gson = Gson()
        val jsonNameList = gson.toJson(newsNameList)
        this.putString("nameList",jsonNameList)
    }
    t.commit()
}