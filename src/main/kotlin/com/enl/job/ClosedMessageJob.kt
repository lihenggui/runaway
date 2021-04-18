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
        if (isIncreased()) {
            bot.run {
                sendMessage("止跌了\uD83C\uDFC3")
                sendRunAwaySticker()
            }
        } else {
            bot.run {
                bot.sendMessage("还行。")
                sendGoodSticker()
            }
        }
    }

    private fun isIncreased(): Boolean {
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
            .toDouble() > 0
    }
}