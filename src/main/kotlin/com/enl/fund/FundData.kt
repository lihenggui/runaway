package com.enl.fund

data class FundData(
    var latestNetWorth: String? = null, // 最新净值
    var latestIncrease: String? = null, // 最新涨幅
    var totalIncrease: String? = null, // 总涨幅
)