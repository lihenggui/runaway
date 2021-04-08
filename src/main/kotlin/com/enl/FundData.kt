package com.enl

import org.jsoup.Jsoup

data class FundData (private val fund: Fund) {
    val sourceUrl = "https://xueqiu.com/p/${fund.id}"
    var latestNetWorth: String? = null // 最新净值
    var latestIncrease: String? = null // 最新涨幅
    var totalIncrease: String? = null // 总涨幅

    init { initData() }

    private fun initData() {
        val doc = Jsoup.connect(sourceUrl).get()
        totalIncrease = doc.select("#cube-info > div.cube-blockmain > div > div.cube-profit-year.fn-clear > span.per").text()
        latestIncrease = doc.select("#cube-info > div.cube-blockmain > div > div.cube-profits.fn-clear > div:nth-child(1) > div.per").text()
        latestNetWorth = doc.select("#cube-info > div.cube-blockmain > div > div.cube-profits.fn-clear > div:nth-child(3) > div.per").text()
    }
}