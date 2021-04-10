package com.enl.job

import com.enl.FundBot
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

class UpdateFundDataJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        println("Execute UpdateFundDataJob at time ${sdf.format(Date())}")
        FundBot().updateFundDataToChannel()
    }
}