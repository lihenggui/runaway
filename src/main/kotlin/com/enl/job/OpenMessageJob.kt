package com.enl.job

import com.enl.FundBot
import org.quartz.Job
import org.quartz.JobExecutionContext
import java.text.SimpleDateFormat
import java.util.*

class OpenMessageJob : Job {
    override fun execute(context: JobExecutionContext?) {
        val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
        println("Execute OpenMessageJob at time ${sdf.format(Date())}")
        FundBot().run {
            sendMessage("开盘了！！！\uD83D\uDCC8")
            sendComeOnSticker()
        }
    }
}