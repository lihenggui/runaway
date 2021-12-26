package com.enl.job

import com.enl.FundBot
import com.enl.OkHttp
import com.enl.news.News
import com.enl.news.SinaNews
import com.enl.news.Tag
import com.google.gson.Gson
import okhttp3.Request
import org.quartz.JobExecutionContext
import java.io.File

class NewsUpdateJob : BaseJob() {

    override fun execute(context: JobExecutionContext?) {
        super.execute(context)
        val news = getNonPublishedNews(getAllFocusedNews()).toMutableList()
        news.addAll(getNonPublishedNews(getAStockMessage()))
        if (news.isNullOrEmpty()) {
            logger.info("No news to publish, return")
            return
        }
        val bot = FundBot()
        news.forEach {
            bot.sendMessage("${it.create_time} ${it.rich_text}")
            checkNewsKeyword(bot, it.rich_text)
        }
        logger.info("Published ${news.size} news")
    }

    private fun checkNewsKeyword(bot: FundBot, text: String) {
        when {
            text.contains("翻红") -> bot.sendNoJumpSticker()
            text.contains("净流出达") ||
                    text.contains("翻绿") ||
                    text.contains("炸板") ||
                    text.contains("转跌") ||
                    text.contains("跌幅扩大") ||
                    text.contains("跌幅达") ||
                    text.contains("抹去") ||
                    text.contains("跌超") ||
                    text.contains("全天净流出") ||
                    text.contains("跳水") -> bot.sendRunAwaySticker()
            text.contains("高走") ||
                    text.contains("流入") ||
                    text.contains("涨幅扩大") ||
                    text.contains("净流入达") -> bot.sendComeOnSticker()
            text.contains("成交额突破") -> bot.sendMessage("冲!")
        }
    }

    private fun getAllFocusedNews(): List<News> {
        // Filter A stock only, add &tag_id=10 param
        val request = Request.Builder()
            .url("http://zhibo.sina.com.cn/api/zhibo/feed?&page=%251&page_size=5&zhibo_id=152")
            .build()
        var allRecentNewsInfo: SinaNews? = null
        try {
            val newsJson = OkHttp.client.newCall(request).execute()?.use { it.body()?.string() }
            allRecentNewsInfo = Gson().fromJson(newsJson, SinaNews::class.java)
        } catch (e: Exception) {
            logger.error("Error in getAllFocusedNews", e)
        }
        return allRecentNewsInfo?.result?.data?.feed?.list?.filter { news ->
            // 9 is a magic number for highlights
            news.tag.contains(Tag(id = "9", ""))
        } ?: listOf()
    }

    private fun getAStockMessage(): List<News> {
        val request = Request.Builder()
            .url("http://zhibo.sina.com.cn/api/zhibo/feed?&page=1&page_size=5&zhibo_id=152&tag_id=10")
            .build()
        var allNews: SinaNews? = null
        try {
            val stockMessage = OkHttp.client.newCall(request).execute()?.use { it.body()?.string() }
            allNews = Gson().fromJson(stockMessage, SinaNews::class.java)
        } catch (e: Exception) {
            logger.error("Error in getAStockMessage", e)
        }
        return allNews?.result?.data?.feed?.list ?: listOf()
    }

    private fun getNonPublishedNews(news: List<News>): List<News> {
        val database = File("data.txt")
        if (!database.exists()) {
            logger.info("No data found, create a new one")
            database.createNewFile()
        }
        val publishedNews = database.readLines()
        val nonPublishedNews = news.filter {
            !publishedNews.contains(it.docurl)
        }
        nonPublishedNews.forEach {
            database.appendText(it.docurl + "\n")
        }
        return nonPublishedNews
    }
}