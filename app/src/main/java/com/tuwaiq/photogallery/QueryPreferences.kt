package com.tuwaiq.photogallery

import android.content.Context
import androidx.preference.PreferenceManager

private const val PREF_SEARCH_QUERY_KEY = "searchQuery"
private const val PREF_LAST_RESULT_ID = "lastResult"
private const val PREF_IS_POLLING = "isPolling"

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

    fun getLastResultId(context: Context):String{
        val pref =  PreferenceManager.getDefaultSharedPreferences(context)

        return pref.getString(PREF_LAST_RESULT_ID,"")!!
    }

    fun setLastResultId(context: Context,lastResultId:String){
        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putString(PREF_LAST_RESULT_ID,lastResultId)
            .apply()
    }

    fun isPolling(context: Context): Boolean {
        return PreferenceManager.getDefaultSharedPreferences(context)
            .getBoolean(PREF_IS_POLLING, false)
    }
    fun setPolling(context: Context, isOn: Boolean) {

        PreferenceManager.getDefaultSharedPreferences(context)
            .edit()
            .putBoolean(PREF_IS_POLLING, isOn)
            .apply()
    }

}