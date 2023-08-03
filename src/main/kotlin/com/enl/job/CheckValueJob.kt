package com.enl.job

import com.enl.FundBot
import com.enl.OkHttp
import com.enl.day.DayInfo
import okhttp3.Request
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

class CheckValueJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            logger.info("Non trading day, skip")
            return
        }
        FundBot().sendMessage(getValueMessage())
    }

    private fun getValueMessage(): String {
        val request = Request.Builder()
            .url("http://hq.sinajs.cn/rn=1618638691874&list=s_sh000001,s_sz399001,s_sz399006")
            .build()
        val data = OkHttp.client
            .newCall(request)
            .execute()
            .use { it.body?.string()?.lines() }
            ?: run {
                logger.error("Cannot get correct value")
                return ""
            }
        logger.info(data.toString())
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