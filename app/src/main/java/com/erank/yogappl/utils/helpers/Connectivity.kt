package com.erank.yogappl.utils.helpers

import android.content.Context
import android.net.ConnectivityManager

object Connectivity {
    fun isNetworkConnected(context: Context): Boolean {
        val service = context.getSystemService(Context.CONNECTIVITY_SERVICE)
        return (service as ConnectivityManager).activeNetworkInfo != null
    }
}