package com.enl

import com.charleskorn.kaml.Yaml
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.sticker
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.network.Response
import com.github.kotlintelegrambot.network.fold
import org.slf4j.LoggerFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class FundBot {
    private val logger = LoggerFactory.getLogger(FundBot::class.java)
    private val config = getConfig()
    private val bot = bot {
        token = config.token
        dispatch {
            command("update") { updateFundDataToChannel() }
            command("run") { sendRunAwaySticker() }
            command("come") { sendComeOnSticker() }
            command("good") { sendGoodSticker() }
            sticker { println(this.media.fileId) }
        }
    }

    fun startListening() {
        logger.info("Start listening events")
        bot.startPolling()
    }

    fun stopListening() {
        logger.info("Stop listening events")
        bot.stopPolling()
    }

    fun updateFundDataToChannel() {
        config.funds.forEach { fund ->
            val summary = getFundData(fund, config)
            val result = sendMessage(summary)
            result.fold({
                logger.info("Message sent")
            }, {
                logger.error("Error while sending message ${it.exception}", it)
            })
        }
    }

    fun sendMessage(summary: String): Pair<retrofit2.Response<Response<Message>?>?, Exception?> {
        logger.info("Send message: $summary")
        return bot.sendMessage(
            chatId = ChatId.fromId(config.channelId.toLong()),
            text = summary,
            disableWebPagePreview = true
        )
    }

    fun sendRunAwaySticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANHYHmjKbVtB57qg0HjEmJYistrk3MAAocAA1u0iA26P5WVji3k3R8E",
            replyMarkup = null
        )
        logger.info("Send run away sticker")
    }

    fun sendGoodSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANOYHp_Vu_BjdTHcVEVn9SnPv1A6gkAAoYAA1u0iA1gJpgUC-QrPh8E",
            replyMarkup = null
        )
        logger.info("Send good sticker")
    }

    fun sendComeOnSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANIYHmjoHcZa-Vsy5Z0iewnk68VpU8AApoAA1u0iA2xuSp5vf9W6h8E",
            replyMarkup = null
        )
        logger.info("Send come on sticker")
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
        logger.info("Query ${fund.name}, $data")
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