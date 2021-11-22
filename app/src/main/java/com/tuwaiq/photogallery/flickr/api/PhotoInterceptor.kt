package com.tuwaiq.photogallery.flickr.api

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response

private const val API_KEY = "d0c796986bdd60cf633bce7affcefdae"
private const val FORMAT = "json"
private const val JSON_CALL_BACK = "1"
private const val EXTRAS = "url_s"
private const val SAFE_SEARCH = "1"

class PhotoInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {

        val originalRequest: Request = chain.request()

        val newUrl:HttpUrl = originalRequest.url().newBuilder()
            .addQueryParameter("api_key", API_KEY)
            .addQueryParameter("format", FORMAT)
            .addQueryParameter("nojsoncallback", JSON_CALL_BACK)
            .addQueryParameter("extras", EXTRAS)
            .addQueryParameter("safesearch", SAFE_SEARCH)
            .build()

        val newRequest: Request = originalRequest.newBuilder()
            .url(newUrl)
            .build()

        return chain.proceed(newRequest)
    }

}