package com.enl

import com.charleskorn.kaml.Yaml
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.text
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.network.fold
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

fun main() {
    val config = getConfig()
    val bot = bot {
        token = config.token
        dispatch {
            command("update") {
                println("Received update command")
                config.funds.forEach { fund ->
                    val data = FundData(fund)
                    val summary = config.template.format(
                        fund.name,
                        getTime(),
                        data.latestNetWorth,
                        data.latestIncrease,
                        data.totalIncrease,
                        data.sourceUrl
                    )
                    println("Query ${fund.name}, $data")
                    val result = bot.sendMessage(chatId = ChatId.fromId(message.chat.id), text = summary, disableWebPagePreview = true)
                    result.fold({
                        println("Message sent")
                    }, {
                        println("Error while sending message ${it.exception}")
                    })
                }
            }
        }
    }
    bot.startPolling()
}

private fun getConfig(): Config {
    val text = File(ClassLoader.getSystemResource("config.yaml").file).readText()
    return Yaml.default.decodeFromString(Config.serializer(), text)
}

private fun getTime(): String {
    val sdf = SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.CHINESE)
    return sdf.format(Date())
}
