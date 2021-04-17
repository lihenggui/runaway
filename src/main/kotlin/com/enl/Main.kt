package com.enl

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
    scheduleOpenSendMessageJob(scheduler)
    scheduleClosedSendMessageJob(scheduler)
    scheduleRegularUpdateChannelJob(scheduler)
    val bot = FundBot()
    bot.startListening()
}

private fun scheduleOpenSendMessageJob(scheduler: Scheduler) {
    println("Scheduling open send message job")
    val jobDetail = JobBuilder.newJob(OpenMessageJob::class.java).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 15 9 ? * *").inTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        )
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}

private fun scheduleClosedSendMessageJob(scheduler: Scheduler) {
    println("Scheduling close send message job")
    val jobDetail = JobBuilder.newJob(ClosedMessageJob::class.java).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 15 ? * *").inTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        )
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}

private fun scheduleRegularUpdateChannelJob(scheduler: Scheduler) {
    println("Scheduling regular fund update job")
    val jobDetail = JobBuilder.newJob(UpdateFundDataJob::class.java).build()
    val trigger = TriggerBuilder.newTrigger()
        .startNow()
        .withSchedule(
            CronScheduleBuilder.cronSchedule("0 0 17 ? * *").inTimeZone(TimeZone.getTimeZone("GMT+8:00"))
        )
        .build()
    scheduler.scheduleJob(jobDetail, trigger)
    scheduler.start()
}