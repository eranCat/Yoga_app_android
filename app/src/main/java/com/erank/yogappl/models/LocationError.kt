package com.erank.yogappl.models

data class LocationError(
    val errorText: String,
    val detailedError: DetailedError,
    val httpStatusCode: Int
) {
    data class DetailedError(
        val code: String,
        val message: String,
        val target: String
    )
}