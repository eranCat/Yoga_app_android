package com.erank.yogappl.data.injection

import android.content.Context
import com.erank.yogappl.data.network.NetworkDataSource
import com.erank.yogappl.data.network.TomTomApi
import com.erank.yogappl.data.repository.*
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.utils.helpers.*
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton

@Module
class DataRepositoryModule {

    @Singleton
    @Provides
    fun provideDataRepository(
        sharedProvider: SharedPrefsHelper,
        dataModelHolder: DataModelsHolder,
        locationHelper: LocationHelper,
        authHelper: AuthHelper,
        storage: StorageManager
    ): Repository =
        RepositoryImpl(
            dataModelHolder,
            sharedProvider,
            locationHelper,
            authHelper,
            storage
        )

    @Provides
    fun provideNetworkDataSource(tomTom: TomTomApi) = NetworkDataSource(tomTom)

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase(context)

    @Provides
    fun provideDataModelHolder(appDB: AppDatabase) = DataModelsHolder(appDB)

    @Provides
    @Singleton
    fun provideTomTomApi(clientBuilder: OkHttpClient.Builder) = TomTomApi.create(clientBuilder)

    @Provides
    fun provideOkHttpClientBuilder(): OkHttpClient.Builder {
        val logging = HttpLoggingInterceptor()
            .setLevel(HttpLoggingInterceptor.Level.BODY)

        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .addInterceptor(logging)
    }

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthHelper(context: Context, auth: FirebaseAuth) = AuthHelper(auth, context)

    @Singleton
    @Provides
    fun provideLocationHelper(context: Context, api: TomTomApi) = LocationHelper(context, api)

    @Singleton
    @Provides
    fun provideMoneyConverter(sharedPrefs: SharedPrefsHelper) = MoneyConverter(sharedPrefs)

    @Singleton
    @Provides
    fun provideCalendarAppHelper(context: Context, prefs: SharedPrefsHelper) =
        CalendarAppHelper(context, prefs)

    @Singleton
    @Provides
    fun provideStorageManager() = StorageManager()

    @Singleton
    @Provides
    fun provideNotificationHelper(context: Context) = NotificationsHelper(context)

}