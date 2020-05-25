package com.erank.yogappl.data.network

import com.erank.yogappl.data.models.CurrencyLayerResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface CurrencyLayerApi {

    @GET(API_LIVE)
    fun getCurrencyCodes(
        @Query("currencies") code: String
    ): Deferred<CurrencyLayerResponse>

    companion object {
        private const val KEY = "ceb2a9d4119b6738d3fa4b8340d94adb"
        private const val BASE_URL = "http://apilayer.net/"
        private const val API_LIVE = "api/live"

        fun create(builder: OkHttpClient.Builder): CurrencyLayerApi {

            val client = builder.addInterceptor(createApiKeyInterceptor())
                .build()

            val adapter = CoroutineCallAdapterFactory()
            val gson = GsonConverterFactory.create()
            return Retrofit.Builder().baseUrl(BASE_URL).client(client)
                .addConverterFactory(gson)
                .addCallAdapterFactory(adapter)
                .build().create(CurrencyLayerApi::class.java)
        }

        private fun createApiKeyInterceptor() = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("access_key", KEY)
                .build()

            val request = original.newBuilder().url(url).build()

            chain.proceed(request)
        }
    }
}