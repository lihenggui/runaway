package com.enl

import com.charleskorn.kaml.Yaml
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.sticker
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.network.fold
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FundBot {
    private val config = getConfig()
    private val bot = bot {
        token = config.token
        dispatch {
            command("update") { updateFundDataToChannel() }
            command("run") { sendRunAwaySticker() }
            command("come") { sendComeOnSticker() }
            sticker { println(this.media.fileId) }
        }
    }

    fun startListening() {
        bot.startPolling()
    }

    fun stopListening() {
        bot.stopPolling()
    }

    fun updateFundDataToChannel() {
        config.funds.forEach { fund ->
            val summary = getFundData(fund, config)
            val result = sendMessage(summary)
            result.fold({
                println("Message sent")
            }, {
                println("Error while sending message ${it.exception}")
            })
        }
    }

    fun sendMessage(summary: String) =
        bot.sendMessage(
            chatId = ChatId.fromId(config.channelId.toLong()),
            text = summary,
            disableWebPagePreview = true
        )

    fun sendRunAwaySticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANHYHmjKbVtB57qg0HjEmJYistrk3MAAocAA1u0iA26P5WVji3k3R8E",
            replyMarkup = null
        )
        println("Send run away sticker")
    }

    fun sendComeOnSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANIYHmjoHcZa-Vsy5Z0iewnk68VpU8AApoAA1u0iA2xuSp5vf9W6h8E",
            replyMarkup = null
        )
        println("Send come on sticker")
    }

    private fun getFundData(fund: Fund, config: Config): String {
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
        return summary
    }

    private fun getConfig(): Config {
        val filename = "config.yaml"
        val configPath = if (File(filename).exists()) {
            File(filename)
        } else {
            File(ClassLoader.getSystemResource("config.yaml").file)
        }
        val text = configPath.readText()
        return Yaml.default.decodeFromString(Config.serializer(), text)
    }

    private fun getTime(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
        return sdf.format(Date())
    }
}