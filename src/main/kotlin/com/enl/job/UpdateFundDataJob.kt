package com.enl.job

import com.enl.FundBot
import com.enl.day.DayInfo
import org.quartz.JobExecutionContext

class UpdateFundDataJob : BaseJob() {
    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        if (!DayInfo.isTradingDay()) {
            println("Non trading day, skip")
            return
        }
        FundBot().updateFundDataToChannel()
    }
}