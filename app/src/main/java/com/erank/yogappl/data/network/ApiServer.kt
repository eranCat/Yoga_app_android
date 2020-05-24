package com.erank.yogappl.data.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import javax.inject.Singleton

@Singleton
interface ApiServer {
//    TODO add retrofit

    companion object {
        operator fun invoke(): ApiServer {
            val okHttpClient = OkHttpClient()

            //TODO fix for server to get default
            //    TODO add this converter to api instead of using it explicitly
            return Retrofit.Builder()
//                .baseUrl(BASE_URL)//TODO add tomtom api url and currency layout api url
                .client(okHttpClient)
// TODO uncomment after adding retrofit
//                .addCallAdapterFactory(CoroutineCallAdapterFactory.invoke())
                .build()
                .create(ApiServer::class.java)
        }
    }
}