package com.erank.yogappl.utils.interfaces


interface TaskCallback<Result, Error> {
    fun onLoading() {}
    fun onSuccess(result: Result? = null)
    fun onFailure(error: Error)
}