package com.enl.fund

import com.enl.Fund
import com.enl.fund.cookie.CookieJar
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

class FundDataGetter(fund: Fund) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    val sourceUrl = "https://xueqiu.com/p/${fund.id}"

    private val okHttpClient = OkHttpClient.Builder()
//        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .cookieJar(CookieJar)
        .build()

    fun getData(): FundData? {
        val body = okHttpClient.newCall(
            Request.Builder()
                .url(sourceUrl)
                .build()
        )
            .execute()
            .body()
            ?.string()
        if (body == null) {
            logger.error("body is null")
            return null
        }
        val doc = Jsoup.parse(body)
        val totalIncrease =
            doc.select("#cube-info > div.cube-blockmain > div > div.cube-profit-year.fn-clear > span.per").text()
        val latestIncrease =
            doc.select("#cube-info > div.cube-blockmain > div > div.cube-profits.fn-clear > div:nth-child(1) > div.per")
                .text()
        val latestNetWorth =
            doc.select("#cube-info > div.cube-blockmain > div > div.cube-profits.fn-clear > div:nth-child(3) > div.per")
                .text()
        if (totalIncrease.isNullOrEmpty() || latestIncrease.isNullOrEmpty() || latestNetWorth.isNullOrEmpty()) {
            logger.debug(body)
            return null
        }
        return FundData(latestNetWorth, latestIncrease, totalIncrease)
    }
}