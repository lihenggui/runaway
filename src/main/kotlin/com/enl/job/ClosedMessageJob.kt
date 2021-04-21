package com.enl.job

import com.enl.FundBot
import com.enl.day.DayInfo
import org.quartz.JobExecutionContext
import java.net.URL

class ClosedMessageJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            logger.info("Non trading day, skip")
            return
        }
        val bot = FundBot()
        when (getIncreasedValue()) {
            in -10.0..-2.0 -> bot.run {
                sendJumpSticker()
            }
            in -2.0..-1.0 -> bot.run {
                sendMessage("不要怕，是技术性调整，不要怕")
                sendAdjustSticker()
            }
            in -1.0..0.0 -> bot.run {
                sendMessage("止跌了\uD83C\uDFC3")
                sendRunAwaySticker()
            }
            in 0.0..1.0 -> bot.run {
                sendMessage("还行。")
                sendGoodSticker()
            }
            in 1.0..10.0 -> bot.run {
                sendKissSticker()
            }
            else -> logger.error("Error value ${getIncreasedValue()}")
        }
    }

    private fun getIncreasedValue(): Double {
        val data = URL("http://hq.sinajs.cn/rn=1618638691874&list=s_sh000001")
            .openStream()
            .bufferedReader()
            .readLine()
        logger.info(data)
        return data.split("=")
            .last()
            .removeSuffix(";")
            .removeSurrounding("\"")
            .split(",")[3]
            .toDouble()
    }
}