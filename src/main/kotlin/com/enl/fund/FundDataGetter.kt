package com.enl.fund

import com.enl.Fund
import com.enl.OkHttp
import okhttp3.Request
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory

class FundDataGetter(fund: Fund) {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val ua = "Mozilla/5.0 (Linux; Android 5.0; SM-N9100 Build/LRX21V) > AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 > Chrome/37.0.0.0 Mobile Safari/537.36 > MicroMessenger/6.0.2.56_r958800.520 NetType/WIFI"
    val sourceUrl = "https://xueqiu.com/p/${fund.id}"

    fun getData(): FundData? {
        val body = OkHttp.client.newCall(
            Request.Builder()
                .url(sourceUrl)
                .header("User-Agent", ua)
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
            logger.info(body)
            return null
        }
        return FundData(latestNetWorth, latestIncrease, totalIncrease)
    }
}

fun main() {
    val getter = FundDataGetter(Fund("ZH2887343", "小白头铁混合"))
    val data = getter.getData()
    println(data)
}