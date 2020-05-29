package com.erank.yogappl.data.network

import com.erank.yogappl.data.models.TomtomLocationsResponse
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import kotlinx.coroutines.Deferred
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Singleton

@Singleton
interface TomTomApi {

    @GET(FUZZY_SEARCH)
    fun searchAsync(
        @Path("query", encoded = true) query: String,
        @Query("countrySet") countryCode: String,
        @Query("language") language: String?,
        @Query("lat") lat: Double?,
        @Query("lon") lon: Double?
    ): Deferred<TomtomLocationsResponse>


    companion object {

        //https://api.tomtom.com/search/2/search/<query>.json?typeahead=true&countrySet=IL&idxSet=POI&key=*****

        private const val BASE_URL = "https://api.tomtom.com/"
        private const val KEY = "hEhWkGvw4i8xlpLfIfY6P3AA1cOBGutJ"
        private const val VERSION = 2
        private const val RESULT_LIMIT = 50
        private const val SEARCH_RADIUS = 500 * 1_000//in meters : 500 KM

        private const val FUZZY_SEARCH =
            "search/$VERSION/search/{query}.json?" +
                    "typeahead=true" +
                    "&limit=$RESULT_LIMIT" +
                    "&radius=$SEARCH_RADIUS"

        fun create(builder: OkHttpClient.Builder): TomTomApi {

            val client = builder
                .addInterceptor(createApiKeyInterceptor())
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(CoroutineCallAdapterFactory())
                .build().create(TomTomApi::class.java)
        }

        private fun createApiKeyInterceptor() = Interceptor { chain ->
            val original = chain.request()
            val originalHttpUrl = original.url()

            val url = originalHttpUrl.newBuilder()
                .addQueryParameter("key", KEY)
                .build()

            val request = original.newBuilder().url(url).build()

            chain.proceed(request)
        }
    }
}