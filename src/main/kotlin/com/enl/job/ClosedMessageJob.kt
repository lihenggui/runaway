package com.enl.job

import com.enl.FundBot
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

class ClosedMessageJob: Job {
    override fun execute(context: JobExecutionContext?) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        println("Execute ClosedMessageJob at time ${sdf.format(Date())}")
        FundBot().sendMessage("止跌了\uD83C\uDFC3")
    }
}