package com.enl.day

import com.google.gson.Gson
import java.net.URL

data class DayInfo(
    val code: Int,
    val holiday: Holiday?,
    val type: Type
) {
    fun isTrading() = type.type == 0

    companion object {
        fun isTradingDay(): Boolean {
            val data = URL("http://timor.tech/api/holiday/info").openStream().bufferedReader()
            val dayInfo = Gson().fromJson(data, DayInfo::class.java)
            println("$dayInfo, trading = ${dayInfo.isTrading()}")
            return dayInfo.isTrading()
        }
    }
}


