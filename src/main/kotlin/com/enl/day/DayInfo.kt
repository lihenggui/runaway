package com.enl.day

import com.enl.OkHttp
import com.google.gson.Gson
import okhttp3.Request

data class DayInfo(
    val code: Int,
    val holiday: Holiday?,
    val type: Type
) {
    fun isTrading() = type.type == 0

    companion object {
        fun isTradingDay(): Boolean {
            val request = Request.Builder().url("http://timor.tech/api/holiday/info").build()
            val data = try {
                OkHttp.client.newCall(request).execute()?.use { it.body()?.string() }
            } catch (e: Exception) {
                e.printStackTrace()
                // Return true to debug
                return true
            }
            val dayInfo = Gson().fromJson(data, DayInfo::class.java)
            println("$dayInfo, trading = ${dayInfo.isTrading()}")
            return dayInfo.isTrading()
        }
    }
}


