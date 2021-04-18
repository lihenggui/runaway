package com.enl.job

import org.quartz.Job
import org.quartz.JobExecutionContext
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.text.SimpleDateFormat
import java.util.*

open class BaseJob : Job {
    protected val logger: Logger = LoggerFactory.getLogger(this::class.java)
    override fun execute(context: JobExecutionContext?) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        logger.info("Execute at time ${sdf.format(Date())}")
    }
}