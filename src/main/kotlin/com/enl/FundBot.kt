package com.enl

import com.enl.config.ConfigHelper
import com.enl.fund.FundDataGetter
import com.github.kotlintelegrambot.bot
import com.github.kotlintelegrambot.dispatch
import com.github.kotlintelegrambot.dispatcher.command
import com.github.kotlintelegrambot.dispatcher.sticker
import com.github.kotlintelegrambot.entities.ChatId
import com.github.kotlintelegrambot.entities.Message
import com.github.kotlintelegrambot.types.TelegramBotResult
import okhttp3.Call
import okhttp3.Callback
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.slf4j.LoggerFactory
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class FundBot {
    private val logger = LoggerFactory.getLogger(FundBot::class.java)
    private val config = ConfigHelper.getConfig()
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
                bot.pinChatMessage(
                    chatId = ChatId.fromId(config.channelId.toLong()),
                    messageId = it.messageId
                )
            }, {
                logger.error("Error while sending message $it")
            })
        }
    }

    fun sendMessage(summary: String): TelegramBotResult<Message> {
        logger.info("Send message: $summary")
        // Send webhook first if defined
        if (config.webhookUrl.isNotEmpty()) {
            sendWebhook(summary)
        }
        return bot.sendMessage(
            chatId = ChatId.fromId(config.channelId.toLong()),
            text = summary,
            disableWebPagePreview = true
        )
    }

    fun sendWebhook(content: String) {
        val json =
            """{"activity": "NewsBot", "icon": "https://xqimg.imedao.com/16c330d0b623f713fd180d89.jpeg!800.jpg", "body": "$content"}"""
        val requestBody = json.toRequestBody("application/json".toMediaTypeOrNull())
        val request = Request.Builder()
            .url(config.webhookUrl)
            .post(requestBody)
            .build()
        val call = OkHttp.client.newCall(request)
        call.enqueue(object : Callback {
            override fun onResponse(call: Call, response: okhttp3.Response) {
                try {
                    response.body?.close()
                } catch (e: Exception) {
                    logger.error("Error closing the response body", e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                logger.error("Fail to send a WebHook", e)
            }
        })
    }

    fun sendRandomGreedManSticker() {
        val stickerSet = bot.getStickerSet("greedtime")
        val stickerId = stickerSet.first?.body()?.result?.stickers?.random()?.fileUniqueId ?: ""
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            stickerId,
            replyMarkup = null
        )
    }

    fun sendKissSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANRYIAZ1Vq66Qh-juWAWQABJ5cGKEYxAALzAAOeCaIH9HRGhoJnizwfBA",
            replyMarkup = null
        )
        logger.info("Send kiss sticker")
    }

    fun sendJumpSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANSYIAZ4wIhbxyRqKAdLi-gQr0daq4AAg8AA54Joge-Z5BowLimGB8E",
            replyMarkup = null
        )
        logger.info("Send jump sticker")
    }

    fun sendNoJumpSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANPYIAWmHcNE-98VdeSz7OQuzlhMh4AAgYAA54JogcBlX8VCz0Bqx8E",
            replyMarkup = null
        )
        logger.info("Send no jump sticker")
    }

    fun sendAdjustSticker() {
        bot.sendSticker(
            chatId = ChatId.fromId(config.channelId.toLong()),
            "CAACAgUAAxkBAANQYIAW94uABBMl1SP_DFK7ZK2cZI8AAhwAA54JogcQR7T9FD8rHx8E",
            replyMarkup = null
        )
        logger.info("Send adjust sticker")
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

    fun getFundData(fund: Fund, config: Config): String {
        val dataGetter = FundDataGetter(fund)
        val data = dataGetter.getData()
        val summary = config.template.format(
            fund.name,
            getTime(),
            data?.latestNetWorth,
            data?.latestIncrease,
            data?.totalIncrease,
            FundDataGetter(fund).sourceUrl
        )
        logger.info("Query ${fund.name}, $data")
        return summary
    }

    private fun getTime(): String {
        val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.CHINESE)
        return sdf.format(Date())
    }
}

fun main() {
    val config = ConfigHelper.getConfig()
    config.funds.forEach {
        println(FundBot().getFundData(it, config))
    }
}