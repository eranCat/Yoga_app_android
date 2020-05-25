package com.erank.yogappl.data.injection

import android.content.Context
import com.erank.yogappl.data.network.ApiServer
import com.erank.yogappl.data.network.NetworkDataSourceImpl
import com.erank.yogappl.data.repository.DataModelsHolder
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.data.repository.RepositoryImpl
import com.erank.yogappl.data.repository.StorageManager
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.utils.helpers.*
import com.google.firebase.auth.FirebaseAuth
import dagger.Module
import dagger.Provides
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
    fun provideNetworkDataSource(api: ApiServer) = NetworkDataSourceImpl(api)

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase(context)

    @Provides
    fun provideDataModelHolder(appDB: AppDatabase) = DataModelsHolder(appDB)

    @Provides
    fun provideRetrofitApi(): ApiServer = ApiServer()

    @Provides
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Singleton
    @Provides
    fun provideAuthHelper(context: Context, auth: FirebaseAuth) = AuthHelper(auth, context)

    @Singleton
    @Provides
    fun provideLocationHelper(context: Context) = LocationHelper(context)

    @Singleton
    @Provides
    fun provideMoneyConverter(sharedPrefs: SharedPrefsHelper) = MoneyConverter(sharedPrefs)

    @Singleton
    @Provides
    fun provideCalendarAppHelper(context: Context, prefs: SharedPrefsHelper) =
        CalendarAppHelper(context, prefs)

    @Singleton
    @Provides
    fun provideStorageManager() =
        StorageManager()

    @Singleton
    @Provides
    fun provideNotificationHelper(context: Context) = NotificationsHelper(context)

}