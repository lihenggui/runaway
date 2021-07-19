package com.enl

import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit

object OkHttp {
    private const val TIMEOUT = 30L
    val client: OkHttpClient = OkHttpClient.Builder()
        .retryOnConnectionFailure(true)
        .connectTimeout(TIMEOUT, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT, TimeUnit.SECONDS)
        .writeTimeout(TIMEOUT, TimeUnit.SECONDS)
        .build()

    fun closeConnections() {
        client.dispatcher().cancelAll()
    }
}