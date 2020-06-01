package com.erank.yogappl.data.network

import com.erank.yogappl.data.models.CurrencyModel
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
interface CurrencyApi {

    @GET(BASE_URL)
    fun getCurrency(
        @Query("from_amount") amount: Number,
        @Query("from") fromCode: String,
        @Query("to") toCode: String
    ): Deferred<CurrencyModel>

    companion object {
        private const val KEY = "98f3612bd8msh61405b2b02f7167p152d9ajsn07e2e26e23e2"
        private const val HOST = "currencyconverter.p.rapidapi.com"
        private const val BASE_URL = "https://$HOST/"

        fun create(builder: OkHttpClient.Builder): CurrencyApi {

            val client = builder.addInterceptor(createApiKeyInterceptor())
                .build()

            val adapter = CoroutineCallAdapterFactory()
            val gson = GsonConverterFactory.create()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(gson)
                .addCallAdapterFactory(adapter)
                .build().create(CurrencyApi::class.java)
        }


        private fun createApiKeyInterceptor() = Interceptor { chain ->
            val original = chain.request()

            val request = original.newBuilder()
                .addHeader("x-rapidapi-key", KEY)
                .addHeader("x-rapidapi-host", HOST)
                .url(original.url())
                .build()

            chain.proceed(request)
        }
    }
}