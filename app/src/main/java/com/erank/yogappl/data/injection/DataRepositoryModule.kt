package com.erank.yogappl.data.injection

import android.content.Context
import com.erank.yogappl.data.network.ApiServer
import com.erank.yogappl.data.network.NetworkDataSourceImpl
import com.erank.yogappl.data.repository.Repository
import com.erank.yogappl.data.repository.RepositoryImpl
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.utils.helpers.SharedPrefsHelper
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataRepositoryModule {

    @Singleton
    @Provides
    fun provideDataRepository(
        appDB: AppDatabase,
        networkDataSource:NetworkDataSource,
        sharedProvider: SharedPrefsHelper
    ): Repository =
        RepositoryImpl(appDB, networkDataSource, sharedProvider)

    @Provides
    fun provideNetworkDataSource(api: ApiServer): NetworkDataSource =
        NetworkDataSourceImpl(api)

    @Singleton
    @Provides
    fun provideDatabase(context: Context): AppDatabase = AppDatabase(context)

    @Provides
    fun provideUserDao(database: AppDatabase) = database.usersDao()

    @Provides
    fun provideLessonDao(database: AppDatabase) = database.lessonsDao()

    @Provides
    fun provideEventDao(database: AppDatabase) = database.eventsDao()

    @Provides
    fun provideRetrofitApi(): ApiServer = ApiServer()
}