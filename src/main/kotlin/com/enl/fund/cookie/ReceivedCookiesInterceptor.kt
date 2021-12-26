package com.enl.fund.cookie

import okhttp3.Interceptor
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.io.IOException

class ReceivedCookiesInterceptor : Interceptor {
    private val logger = LoggerFactory.getLogger("ReceivedCookiesInterceptor")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalResponse = chain.proceed(chain.request())
        val cookies = originalResponse.headers("Set-Cookie")
        logger.info("Received cookies: $cookies")
        return originalResponse
    }
}