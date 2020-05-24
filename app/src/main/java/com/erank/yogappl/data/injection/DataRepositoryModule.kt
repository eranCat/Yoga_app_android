package com.erank.yogappl.data.injection

import android.content.Context
import com.erank.yogappl.data.network.ApiServer
import com.erank.yogappl.data.network.NetworkDataSourceImpl
import com.erank.yogappl.data.repository.DataModelsHolder
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
        sharedProvider: SharedPrefsHelper,
        dataModelHolder: DataModelsHolder
    ): Repository =
        RepositoryImpl(dataModelHolder, sharedProvider)

    @Provides
    fun provideNetworkDataSource(api: ApiServer) = NetworkDataSourceImpl(api)

    @Singleton
    @Provides
    fun provideDatabase(context: Context) = AppDatabase(context)

    @Provides
    fun provideDataModelHolder(appDB: AppDatabase) = DataModelsHolder(appDB)

    @Provides
    fun provideRetrofitApi(): ApiServer = ApiServer()
}