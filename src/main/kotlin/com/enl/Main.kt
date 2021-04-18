package com.enl

import com.enl.job.*
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import java.util.*

fun main() {
    scheduleJobs()
    val bot = FundBot()
    bot.startListening()
}

private fun scheduleJobs() {
    val scheduler = StdSchedulerFactory().scheduler
    scheduleJob(scheduler, OpenMessageJob::class.java, "0 15 9 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "0 25 9 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "0 30 11 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "0 0 15 ? * *")
    scheduleJob(scheduler, ClosedMessageJob::class.java, "0 0 15 ? * *")
    scheduleJob(scheduler, UpdateFundDataJob::class.java, "0 0 17 ? * *")
}

private fun scheduleJob(scheduler: Scheduler, job: Class<out BaseJob>, cornExpression: String) {
    println("Scheduling job $job at $cornExpression")
    val jobDetail = JobBuilder.newJob(job).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(CronScheduleBuilder.cronSchedule(cornExpression))
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}