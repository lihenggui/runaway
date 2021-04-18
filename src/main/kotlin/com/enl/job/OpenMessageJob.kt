package com.enl.job

import com.enl.FundBot
import com.enl.day.DayInfo
import org.quartz.JobExecutionContext

class OpenMessageJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            logger.info("Non trading day, skip")
            return
        }
        FundBot().run {
            sendMessage("开盘了！！！\uD83D\uDCC8")
            sendComeOnSticker()
        }
    }
}