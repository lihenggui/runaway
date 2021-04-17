package com.enl.job

import com.enl.FundBot
import com.enl.day.DayInfo
import org.quartz.JobExecutionContext
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

class CheckValueJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            logger.debug("Non trading day, skip")
            return
        }
        FundBot().sendMessage(getValueMessage())
    }

    private fun getValueMessage(): String {
        val data = URL("http://hq.sinajs.cn/rn=1618638691874&list=s_sh000001,s_sz399001,s_sz399006")
            .openStream()
            .bufferedReader()
            .readLines()
        logger.debug(data.toString())
        val szzsData = getValueAndIncreaseFromString(data[0])
        val szzsValue = szzsData.first
        val szzsIncrease = szzsData.second
        val szczData = getValueAndIncreaseFromString(data[1])
        val szczValue = szczData.first
        val szczIncrease = szczData.second
        val cybzData = getValueAndIncreaseFromString(data[2])
        val cybzValue = cybzData.first
        val cybzIncrease = cybzData.second
        val date = SimpleDateFormat("yyyy年MM月dd日 HH时mm分ss秒").format(Date())
        return "${date}，沪指报${szzsValue}点，涨跌幅$szzsIncrease%；深证成指报${szczValue}点，涨跌幅$szczIncrease%；创业板指报${cybzValue}点，涨跌幅$cybzIncrease%。"
    }

    private fun getValueAndIncreaseFromString(data: String): Pair<String, String> {
        val stockData = data.split("=")
            .last()
            .removeSuffix(";")
            .removeSurrounding("\"")
            .split(",")
        return Pair(stockData[1], stockData[3])
    }
}