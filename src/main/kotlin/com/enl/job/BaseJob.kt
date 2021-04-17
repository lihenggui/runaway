package com.enl.job

import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

open class BaseJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        println("Execute ClosedMessageJob at time ${sdf.format(Date())}")
    }
}