package com.enl

import com.enl.job.BaseJob
import com.enl.job.ClosedMessageJob
import com.enl.job.OpenMessageJob
import com.enl.job.UpdateFundDataJob
import org.quartz.CronScheduleBuilder
import org.quartz.JobBuilder
import org.quartz.Scheduler
import org.quartz.TriggerBuilder
import org.quartz.impl.StdSchedulerFactory
import java.util.*

fun main() {
    val scheduler = StdSchedulerFactory().scheduler
    scheduleJob(scheduler, OpenMessageJob::class.java, "0 15 9 ? * *")
    scheduleJob(scheduler, ClosedMessageJob::class.java, "0 15 9 ? * *")
    scheduleJob(scheduler, UpdateFundDataJob::class.java, "0 0 17 ? * *")
    val bot = FundBot()
    bot.startListening()
}

private fun scheduleJob(scheduler: Scheduler, job: Class<out BaseJob>, cornExpression: String) {
    println("Scheduling job $job at $cornExpression")
    val jobDetail = JobBuilder.newJob(job).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(
            CronScheduleBuilder.cronSchedule(cornExpression).inTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        )
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}