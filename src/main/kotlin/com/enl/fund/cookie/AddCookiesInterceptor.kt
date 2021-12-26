package com.enl.fund.cookie

import com.enl.config.ConfigHelper
import okhttp3.Interceptor
import okhttp3.Response
import org.slf4j.LoggerFactory
import java.io.IOException

class AddCookiesInterceptor : Interceptor {
    private val logger = LoggerFactory.getLogger("ReceivedCookiesInterceptor")

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (request.url().host().contains("xueqiu")) {
            val builder = request.newBuilder()
            val xqCookie = ConfigHelper.getConfig().xqCookie
            builder.addHeader("Cookie", xqCookie)
            logger.info("Cookie added")
            return chain.proceed(builder.build())
        }
        return chain.proceed(request)
    }
}