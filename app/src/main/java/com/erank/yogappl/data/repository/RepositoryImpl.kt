package com.erank.yogappl.data.repository

import com.erank.yogappl.data.injection.NetworkDataSource
import com.erank.yogappl.data.room.AppDatabase
import com.erank.yogappl.utils.helpers.SharedPrefsHelper
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    val appDB: AppDatabase,
    val networkDataSource: NetworkDataSource,
    val sharedProvider: SharedPrefsHelper
) : Repository {
    companion object {
        const val TAG = "Repository"
    }


}