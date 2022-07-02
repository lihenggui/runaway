package com.enl

import com.enl.day.DayInfo
import com.enl.job.*
import org.quartz.*
import org.quartz.impl.StdSchedulerFactory
import java.util.*

fun main() {
    TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"))
    DayInfo.isTradingDay()
//    scheduleJobs()
//    FundBot().startListening()
}

private fun scheduleJobs() {
    val scheduler = StdSchedulerFactory().scheduler
    scheduleJob(scheduler, OpenMessageJob::class.java, "0 15 9 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "10 30 9 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "0 30 11 ? * *")
    scheduleJob(scheduler, CheckValueJob::class.java, "30 0 15 ? * *")
    scheduleJob(scheduler, ClosedMessageJob::class.java, "0 1 15 ? * *")
    scheduleJob(scheduler, UpdateFundDataJob::class.java, "0 0 17 ? * *")
    scheduleRepeatJob(scheduler, NewsUpdateJob::class.java, 1)
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

private fun scheduleRepeatJob(scheduler: Scheduler, job: Class<out BaseJob>, time: Int) {
    println("Scheduling job $job repeat at $time")
    val jobDetail = JobBuilder.newJob(job).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(SimpleScheduleBuilder.simpleSchedule().withIntervalInMinutes(time).repeatForever())
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}