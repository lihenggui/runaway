package com.enl.job

import com.enl.FundBot
import com.enl.day.DayInfo
import org.quartz.JobExecutionContext

class ClosedMessageJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            println("Non trading day, skip")
            return
        }
        FundBot().run {
            sendMessage("止跌了\uD83C\uDFC3")
            sendRunAwaySticker()
        }
    }
}