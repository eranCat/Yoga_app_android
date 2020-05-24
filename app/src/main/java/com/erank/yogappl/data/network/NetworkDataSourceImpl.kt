package com.erank.yogappl.data.network

import com.erank.yogappl.data.injection.NetworkDataSource
import javax.inject.Inject

class NetworkDataSourceImpl @Inject constructor(api: ApiServer) :
    NetworkDataSource {

}
