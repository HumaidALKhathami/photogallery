package com.tuwaiq.photogallery

import android.content.Context
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY_KEY = "searchQuery"

object QueryPreferences {

    fun setStoredQuery(context: Context, query:String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_SEARCH_QUERY_KEY,query)
            .apply()
    }

    fun getStoredQuery(context: Context):String{

        val pref = PreferenceManager.getDefaultSharedPreferences(context)


        return pref.getString(PREF_SEARCH_QUERY_KEY,"")!!
    }

}